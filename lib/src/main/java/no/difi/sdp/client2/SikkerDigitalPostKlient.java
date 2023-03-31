/*
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
package no.difi.sdp.client2;

import no.difi.sdp.client2.domain.Databehandler;
import no.difi.sdp.client2.domain.Forsendelse;
import no.difi.sdp.client2.domain.exceptions.SendException;
import no.difi.sdp.client2.domain.kvittering.ForretningsKvittering;
import no.difi.sdp.client2.domain.kvittering.KvitteringForespoersel;
import no.difi.sdp.client2.internal.Billable;
import no.difi.sdp.client2.internal.CertificateValidator;
import no.difi.sdp.client2.internal.DigipostMessageSenderFacade;
import no.difi.sdp.client2.internal.EbmsForsendelseBuilder;
import no.difi.sdp.client2.internal.KvitteringBuilder;
import no.difi.sdp.client2.util.CryptoChecker;
import no.digipost.api.representations.EbmsApplikasjonsKvittering;
import no.digipost.api.representations.EbmsForsendelse;
import no.digipost.api.representations.EbmsPullRequest;
import no.digipost.api.representations.KanBekreftesSomBehandletKvittering;
import no.digipost.api.representations.TransportKvittering;
import org.springframework.ws.client.core.WebServiceTemplate;

public class SikkerDigitalPostKlient {

    private final Databehandler databehandler;
    private final EbmsForsendelseBuilder ebmsForsendelseBuilder;
    private final KvitteringBuilder kvitteringBuilder;
    private final DigipostMessageSenderFacade digipostMessageSenderFacade;
    private final KlientKonfigurasjon klientKonfigurasjon;

    /**
     * @param databehandler       parten som er ansvarlig for den tekniske utførelsen av sendingen.
     *                            Se <a href="http://begrep.difi.no/SikkerDigitalPost/forretningslag/Aktorer">oversikt over aktører</a> for mer informasjon.
     * @param klientKonfigurasjon Oppsett for blant annet oppkoblingen mot meldingsformidler og interceptorer for å få ut data som sendes.
     */
    public SikkerDigitalPostKlient(Databehandler databehandler, KlientKonfigurasjon klientKonfigurasjon) {
        CryptoChecker.checkCryptoPolicy();

        this.ebmsForsendelseBuilder = new EbmsForsendelseBuilder();
        this.kvitteringBuilder = new KvitteringBuilder();
        this.digipostMessageSenderFacade = new DigipostMessageSenderFacade(databehandler, klientKonfigurasjon);

        this.klientKonfigurasjon = klientKonfigurasjon;
        this.databehandler = databehandler;

        CertificateValidator.validate(klientKonfigurasjon.getMiljo(), databehandler.noekkelpar.getVirksomhetssertifikat().getX509Certificate());
    }

    /**
     * Sender en forsendelse til meldingsformidler. Dersom noe feilet i sendingen til meldingsformidler, vil det kastes en exception med beskrivende feilmelding.
     *
     * @param forsendelse Et objekt som har all informasjon klar til å kunne sendes (mottakerinformasjon, sertifikater, dokumenter mm),
     *                    enten digitalt eller fyisk.
     */
    public SendResultat send(Forsendelse forsendelse) throws SendException {
        Billable<EbmsForsendelse> forsendelseBundleWithBillableBytes = ebmsForsendelseBuilder.buildEbmsForsendelse(databehandler, klientKonfigurasjon.getMeldingsformidlerOrganisasjon(), forsendelse);
        TransportKvittering kvittering = digipostMessageSenderFacade.send(forsendelseBundleWithBillableBytes.entity);

        return new SendResultat(kvittering.messageId, kvittering.refToMessageId, forsendelseBundleWithBillableBytes.billableBytes);
    }

    /**
     * Forespør kvittering for forsendelser. Kvitteringer blir tilgjengeliggjort etterhvert som de er klare i meldingsformidler.
     * Det er ikke mulig å etterspørre kvittering for en spesifikk forsendelse.
     * <p>
     * Dersom det ikke er tilgjengelige kvitteringer skal det ventes følgende tidsintervaller før en ny forespørsel gjøres:
     * <dl>
     * <dt>normal</dt>
     * <dd>Minimum 10 minutter</dd>
     * <dt>prioritert</dt>
     * <dd>Minimum 1 minutt</dd>
     * </dl>
     */
    public ForretningsKvittering hentKvittering(KvitteringForespoersel kvitteringForespoersel) throws SendException {
        return hentKvitteringOgBekreftForrige(kvitteringForespoersel, null);
    }

    /**
     * Forespør kvittering for forsendelser med mulighet til å samtidig bekrefte på forrige kvittering for å slippe å kjøre eget kall for bekreft.
     * Kvitteringer blir tilgjengeliggjort etterhvert som de er klare i meldingsformidler. Det er ikke mulig å etterspørre kvittering for en
     * spesifikk forsendelse.
     * <p>
     * Dersom det ikke er tilgjengelige kvitteringer skal det ventes følgende tidsintervaller før en ny forespørsel gjøres:
     * <dl>
     * <dt>normal</dt>
     * <dd>Minimum 10 minutter</dd>
     * <dt>prioritert</dt>
     * <dd>Minimum 1 minutt</dd>
     * </dl>
     */
    public ForretningsKvittering hentKvitteringOgBekreftForrige(KvitteringForespoersel kvitteringForespoersel, KanBekreftesSomBehandletKvittering forrigeKvittering) throws SendException {
        EbmsPullRequest ebmsPullRequest = kvitteringBuilder.buildEbmsPullRequest(klientKonfigurasjon.getMeldingsformidlerOrganisasjon(), kvitteringForespoersel);

        EbmsApplikasjonsKvittering ebmsApplikasjonsKvittering;
        if (forrigeKvittering == null) {
            ebmsApplikasjonsKvittering = digipostMessageSenderFacade.hentKvittering(ebmsPullRequest);
        } else {
            ebmsApplikasjonsKvittering = digipostMessageSenderFacade.hentKvittering(ebmsPullRequest, forrigeKvittering);
        }

        if (ebmsApplikasjonsKvittering == null) {
            return null;
        }

        return kvitteringBuilder.buildForretningsKvittering(ebmsApplikasjonsKvittering);
    }

    /**
     * Bekreft mottak av forretningskvittering gjennom {@link #hentKvittering(KvitteringForespoersel)}.
     * {@link #hentKvittering(KvitteringForespoersel)} kommer ikke til å returnere en ny kvittering før mottak av den forrige er bekreftet.
     * <p>
     * Dette legger opp til følgende arbeidsflyt:
     * <ol>
     * <li>{@link #hentKvittering(KvitteringForespoersel)}</li>
     * <li>Gjør intern prosessering av kvitteringen (lagre til database, og så videre)</li>
     * <li>Bekreft mottak av kvittering</li>
     * </ol>
     */
    public void bekreft(KanBekreftesSomBehandletKvittering forrigeKvittering) throws SendException {
        digipostMessageSenderFacade.bekreft(forrigeKvittering);
    }

    /**
     * Registrer egen ExceptionMapper.
     */
    public void setExceptionMapper(ExceptionMapper exceptionMapper) {
        this.digipostMessageSenderFacade.setExceptionMapper(exceptionMapper);
    }

    /**
     * Hent ut Spring {@code WebServiceTemplate} som er konfigurert internt, og brukes av biblioteket
     * til kommunikasjon med meldingsformidler. Ved hjelp av denne instansen kan man f.eks. sette opp en
     * {@code MockWebServiceServer} for bruk i tester.
     * <p>
     * Man vil ikke under normale omstendigheter aksessere denne i produksjonskode.
     *
     * @return Spring {@code WebServiceTemplate} som er konfigurert internt i klientbiblioteket
     *
     * @see <a href="https://docs.spring.io/spring-ws/docs/3.0.7.RELEASE/reference/#_using_the_client_side_api">Spring WS - 6.2. Using the client-side API</a>
     * @see <a href="https://docs.spring.io/spring-ws/docs/3.0.7.RELEASE/reference/#_client_side_testing">Spring WS - 6.3. Client-side testing</a>
     */
    public WebServiceTemplate getMeldingTemplate() {
        return digipostMessageSenderFacade.getMeldingTemplate();
    }

}
