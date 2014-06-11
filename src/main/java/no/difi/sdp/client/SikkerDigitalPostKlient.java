/**
 * Copyright (C) Posten Norge AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package no.difi.sdp.client;

import no.difi.sdp.client.domain.Avsender;
import no.difi.sdp.client.domain.Forsendelse;
import no.difi.sdp.client.domain.kvittering.ForretningsKvittering;
import no.difi.sdp.client.domain.kvittering.KvitteringForespoersel;
import no.difi.sdp.client.internal.EbmsForsendelseBuilder;
import no.difi.sdp.client.internal.KvitteringBuilder;
import no.difi.sdp.client.util.CryptoChecker;
import no.digipost.api.MessageSender;
import no.digipost.api.representations.EbmsAktoer;
import no.digipost.api.representations.EbmsApplikasjonsKvittering;
import no.digipost.api.representations.EbmsForsendelse;
import no.digipost.api.representations.EbmsPullRequest;
import no.digipost.api.representations.Organisasjonsnummer;

public class SikkerDigitalPostKlient {

    private final Organisasjonsnummer digipostMeldingsformidler = new Organisasjonsnummer("984661185");

    private final MessageSender messageSender;
    private final Avsender avsender;
    private final EbmsForsendelseBuilder ebmsForsendelseBuilder;
    private final KvitteringBuilder kvitteringBuilder;

    public SikkerDigitalPostKlient(Avsender avsender, KlientKonfigurasjon konfigurasjon) {
        CryptoChecker.checkCryptoPolicy();

        ebmsForsendelseBuilder = new EbmsForsendelseBuilder();
        kvitteringBuilder = new KvitteringBuilder();

        this.avsender = avsender;

        MessageSender.Builder msBuilder = MessageSender.create(konfigurasjon.getMeldingsformidlerRoot().toString(),
                avsender.getNoekkelpar().getKeyStoreInfo(),
                EbmsAktoer.avsender(avsender.getOrganisasjonsnummer()),
                EbmsAktoer.meldingsformidler(digipostMeldingsformidler))
                .withConnectTimeout((int) konfigurasjon.getConnectTimeoutInMillis())
                .withSocketTimeout((int) konfigurasjon.getSocketTimeoutInMillis())
                .withConnectionRequestTimeout((int) konfigurasjon.getConnectionRequestTimeoutInMillis());

        if (konfigurasjon.useProxy()) {
            msBuilder.withHttpProxy(konfigurasjon.getProxyHost(), konfigurasjon.getProxyPort());
        }

        messageSender = msBuilder.build();
    }

    /**
     * Sender en forsendelse til meldingsformidler. En forsendelse kan være digital post eller fysisk post.
     * Dersom noe feilet i sendingen til meldingsformidler, vil det kastes en exception med beskrivende feilmelding.
     *
     * @param forsendelse Et objekt som har all informasjon klar til å kunne sendes (mottakerinformasjon, sertifikater, dokumenter mm),
     *                    enten digitalt eller fyisk.
     */
    public void send(Forsendelse forsendelse) {
        if (!forsendelse.isDigitalPostforsendelse()) {
            throw new UnsupportedOperationException("Fysiske forsendelser er ikke støttet");
        }

        EbmsForsendelse ebmsForsendelse = ebmsForsendelseBuilder.buildEbmsForsendelse(avsender, digipostMeldingsformidler, forsendelse);
        messageSender.send(ebmsForsendelse);
    }

    /**
     * Forespør kvittering for forsendelser. Kvitteringer blir tilgjengeliggjort etterhvert som de er klare i meldingsformidler.
     * Det er ikke mulig å etterspørre kvittering for en spesifikk forsendelse.
     *
     * Dersom det ikke er tilgjengelige kvitteringer skal det ventes følgende tidsintervaller før en ny forespørsel gjøres:
     * <dl>
     *     <dt>normal</dt>
     *     <dd>Minimum 10 minutter</dd>
     *
     *     <dt>prioritert</dt>
     *     <dd>Minimum 1 minutt</dd>
     * </dl>
     *
     */
    public ForretningsKvittering hentKvittering(KvitteringForespoersel kvitteringForespoersel) {
        return hentKvitteringOgBekreftForrige(kvitteringForespoersel, null);
    }

    /**
     * Forespør kvittering for forsendelser med mulighet til å samtidig bekrefte på forrige kvittering for å slippe å kjøre eget kall for bekreft.
     * Kvitteringer blir tilgjengeliggjort etterhvert som de er klare i meldingsformidler. Det er ikke mulig å etterspørre kvittering for en
     * spesifikk forsendelse.
     *
     * Dersom det ikke er tilgjengelige kvitteringer skal det ventes følgende tidsintervaller før en ny forespørsel gjøres:
     * <dl>
     *     <dt>normal</dt>
     *     <dd>Minimum 10 minutter</dd>
     *
     *     <dt>prioritert</dt>
     *     <dd>Minimum 1 minutt</dd>
     * </dl>
     *
     */
    public ForretningsKvittering hentKvitteringOgBekreftForrige(KvitteringForespoersel kvitteringForespoersel, ForretningsKvittering forrigeKvittering) {
        EbmsPullRequest ebmsPullRequest = kvitteringBuilder.buildEbmsPullRequest(digipostMeldingsformidler, kvitteringForespoersel.getPrioritet());

        if (forrigeKvittering == null) {
            return kvitteringBuilder.buildForretningsKvittering(messageSender.hentKvittering(ebmsPullRequest));
        } else {
            return kvitteringBuilder.buildForretningsKvittering(messageSender.hentKvittering(ebmsPullRequest, forrigeKvittering.applikasjonsKvittering));
        }
    }

    /**
     * Bekreft mottak av forretningskvittering gjennom {@link #hentKvittering(KvitteringForespoersel)}.
     * {@link #hentKvittering(KvitteringForespoersel)} kommer ikke til å returnere en ny kvittering før mottak av den forrige er bekreftet.
     *
     * Dette legger opp til følgende arbeidsflyt:
     * <ol>
     *     <li>{@link #hentKvittering(KvitteringForespoersel)}</li>
     *     <li>Gjør intern prosessering av kvitteringen (lagre til database, og så videre)</li>
     *     <li>Bekreft mottak av kvittering</li>
     * </ol>
     */
    public void bekreft(ForretningsKvittering forrigeKvittering) {
        EbmsApplikasjonsKvittering kvittering = forrigeKvittering.applikasjonsKvittering;
        messageSender.bekreft(kvittering);
    }

}
