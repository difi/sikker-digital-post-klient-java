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
import no.difi.sdp.client.domain.exceptions.SendException;
import no.difi.sdp.client.domain.kvittering.ForretningsKvittering;
import no.difi.sdp.client.domain.kvittering.KvitteringForespoersel;
import no.difi.sdp.client.internal.DigipostMessageSenderFacade;
import no.difi.sdp.client.internal.EbmsForsendelseBuilder;
import no.difi.sdp.client.internal.KvitteringBuilder;
import no.difi.sdp.client.util.CryptoChecker;
import no.digipost.api.representations.EbmsApplikasjonsKvittering;
import no.digipost.api.representations.EbmsForsendelse;
import no.digipost.api.representations.EbmsPullRequest;

public class SikkerDigitalPostKlient {

    private final Avsender avsender;
    private final EbmsForsendelseBuilder ebmsForsendelseBuilder;
    private final KvitteringBuilder kvitteringBuilder;
    private final DigipostMessageSenderFacade digipostMessageSenderFacade;
    private final KlientKonfigurasjon konfigurasjon;

    public SikkerDigitalPostKlient(Avsender avsender, KlientKonfigurasjon konfigurasjon) {
        CryptoChecker.checkCryptoPolicy();

        this.ebmsForsendelseBuilder = new EbmsForsendelseBuilder();
        this.kvitteringBuilder = new KvitteringBuilder();
        this.digipostMessageSenderFacade = new DigipostMessageSenderFacade(avsender, konfigurasjon);

        this.konfigurasjon = konfigurasjon;
        this.avsender = avsender;
    }

    /**
     * Sender en forsendelse til meldingsformidler. Dersom noe feilet i sendingen til meldingsformidler, vil det kastes en exception med beskrivende feilmelding.
     *
     * @param forsendelse Et objekt som har all informasjon klar til å kunne sendes (mottakerinformasjon, sertifikater, dokumenter mm),
     *                    enten digitalt eller fyisk.
     */
    public void send(Forsendelse forsendelse) throws SendException {
        EbmsForsendelse ebmsForsendelse = ebmsForsendelseBuilder.buildEbmsForsendelse(avsender, konfigurasjon.getMeldingsformidlerOrganisasjon(), forsendelse);
        digipostMessageSenderFacade.send(ebmsForsendelse);
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
    public ForretningsKvittering hentKvittering(KvitteringForespoersel kvitteringForespoersel) throws SendException {
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
    public ForretningsKvittering hentKvitteringOgBekreftForrige(KvitteringForespoersel kvitteringForespoersel, ForretningsKvittering forrigeKvittering) throws SendException {
        EbmsPullRequest ebmsPullRequest = kvitteringBuilder.buildEbmsPullRequest(konfigurasjon.getMeldingsformidlerOrganisasjon(), avsender, kvitteringForespoersel);

        EbmsApplikasjonsKvittering applikasjonsKvittering;
        if (forrigeKvittering == null) {
            applikasjonsKvittering = digipostMessageSenderFacade.hentKvittering(ebmsPullRequest);
        } else {
            applikasjonsKvittering = digipostMessageSenderFacade.hentKvittering(ebmsPullRequest, forrigeKvittering.applikasjonsKvittering);
        }

        if (applikasjonsKvittering == null) {
            return null;
        }

        return kvitteringBuilder.buildForretningsKvittering(applikasjonsKvittering);
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
    public void bekreft(ForretningsKvittering forrigeKvittering) throws SendException {
        EbmsApplikasjonsKvittering kvittering = forrigeKvittering.applikasjonsKvittering;
        digipostMessageSenderFacade.bekreft(kvittering);
    }

    /**
     * Registrer egen ExceptionMapper.
     */
    public void setExceptionMapper(ExceptionMapper exceptionMapper) {
        this.digipostMessageSenderFacade.setExceptionMapper(exceptionMapper);
    }

}
