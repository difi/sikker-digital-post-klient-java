package no.difi.sdp.client2;

import no.difi.sdp.client2.domain.Forsendelse;
import no.difi.sdp.client2.domain.TekniskAvsender;
import no.difi.sdp.client2.domain.exceptions.SendException;
import no.difi.sdp.client2.domain.kvittering.ForretningsKvittering;
import no.difi.sdp.client2.domain.kvittering.KvitteringForespoersel;
import no.difi.sdp.client2.internal.DigipostMessageSenderFacade;
import no.difi.sdp.client2.internal.EbmsForsendelseBuilder;
import no.difi.sdp.client2.internal.KvitteringBuilder;
import no.difi.sdp.client2.util.CryptoChecker;
import no.digipost.api.representations.EbmsApplikasjonsKvittering;
import no.digipost.api.representations.EbmsForsendelse;
import no.digipost.api.representations.EbmsPullRequest;

public class SikkerDigitalPostKlient {

    private final TekniskAvsender tekniskAvsender;
    private final EbmsForsendelseBuilder ebmsForsendelseBuilder;
    private final KvitteringBuilder kvitteringBuilder;
    private final DigipostMessageSenderFacade digipostMessageSenderFacade;
    private final KlientKonfigurasjon konfigurasjon;

    /**
     *
     * @param tekniskAvsender teknisk avsender er den parten som har ansvarlig for den tekniske utførelsen av sendingen.
     *                        Teknisk avsender er den aktøren som står for utførelsen av den tekniske sendingen.
     *                        Hvis sendingen utføres av en databehandler vil dette være databehandleren.
     *                        Hvis sendingen utføres av behandlingsansvarlige selv er dette den behandlingsansvarlige.
     *                        Se <a href="http://begrep.difi.no/SikkerDigitalPost/forretningslag/Aktorer">oversikt over aktører</a> for mer informasjon.
     */
    public SikkerDigitalPostKlient(TekniskAvsender tekniskAvsender, KlientKonfigurasjon konfigurasjon) {
        CryptoChecker.checkCryptoPolicy();

        this.ebmsForsendelseBuilder = new EbmsForsendelseBuilder();
        this.kvitteringBuilder = new KvitteringBuilder();
        this.digipostMessageSenderFacade = new DigipostMessageSenderFacade(tekniskAvsender, konfigurasjon);

        this.konfigurasjon = konfigurasjon;
        this.tekniskAvsender = tekniskAvsender;
    }

    /**
     * Sender en forsendelse til meldingsformidler. Dersom noe feilet i sendingen til meldingsformidler, vil det kastes en exception med beskrivende feilmelding.
     *
     * @param forsendelse Et objekt som har all informasjon klar til å kunne sendes (mottakerinformasjon, sertifikater, dokumenter mm),
     *                    enten digitalt eller fyisk.
     */
    public void send(Forsendelse forsendelse) throws SendException {
        EbmsForsendelse ebmsForsendelse = ebmsForsendelseBuilder.buildEbmsForsendelse(tekniskAvsender, konfigurasjon.getMeldingsformidlerOrganisasjon(), forsendelse);
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
        EbmsPullRequest ebmsPullRequest = kvitteringBuilder.buildEbmsPullRequest(konfigurasjon.getMeldingsformidlerOrganisasjon(), kvitteringForespoersel);

        EbmsApplikasjonsKvittering ebmsApplikasjonsKvittering;
        if (forrigeKvittering == null) {
            ebmsApplikasjonsKvittering = digipostMessageSenderFacade.hentKvittering(ebmsPullRequest);
        } else {
            ebmsApplikasjonsKvittering = null; //digipostMessageSenderFacade.hentKvittering(ebmsPullRequest, forrigeKvittering.applikasjonsKvittering);
        }

        if (ebmsApplikasjonsKvittering == null) {
            return null;
        }

        return kvitteringBuilder.buildForretningsKvittering(ebmsApplikasjonsKvittering);
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
//        EbmsApplikasjonsKvittering kvittering = forrigeKvittering.applikasjonsKvittering;
//        digipostMessageSenderFacade.bekreft(kvittering);
    }

    /**
     * Registrer egen ExceptionMapper.
     */
    public void setExceptionMapper(ExceptionMapper exceptionMapper) {
        this.digipostMessageSenderFacade.setExceptionMapper(exceptionMapper);
    }

}
