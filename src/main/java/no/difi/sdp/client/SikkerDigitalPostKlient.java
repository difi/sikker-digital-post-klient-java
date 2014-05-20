package no.difi.sdp.client;

import no.difi.sdp.client.domain.Avsender;
import no.difi.sdp.client.domain.Forsendelse;
import no.difi.sdp.client.domain.kvittering.BekreftelsesKvittering;
import no.difi.sdp.client.domain.kvittering.ForretningsKvittering;
import no.difi.sdp.client.domain.kvittering.KvitteringForespoersel;
import no.difi.sdp.client.internal.EbmsForsendelseBuilder;
import no.difi.sdp.client.internal.KvitteringBuilder;
import no.posten.dpost.offentlig.api.MessageSender;
import no.posten.dpost.offentlig.api.representations.EbmsApplikasjonsKvittering;
import no.posten.dpost.offentlig.api.representations.EbmsForsendelse;
import no.posten.dpost.offentlig.api.representations.EbmsPullRequest;
import no.posten.dpost.offentlig.api.representations.Organisasjonsnummer;

public class SikkerDigitalPostKlient {

    private final Organisasjonsnummer digipostMeldingsformidler = new Organisasjonsnummer("TODO");
    private final MessageSender messageSender;
    private final Avsender avsender;
    private final EbmsForsendelseBuilder ebmsForsendelseBuilder;
    private final KvitteringBuilder kvitteringBuilder;

    public SikkerDigitalPostKlient(Avsender avsender, KlientKonfigurasjon konfigurasjon) {
        ebmsForsendelseBuilder = new EbmsForsendelseBuilder();
        kvitteringBuilder = new KvitteringBuilder();

        this.avsender = avsender;
        try {
            messageSender = MessageSender.create(konfigurasjon.getMeldingsformidlerRoot().toString(),
                    avsender.getNoekkelpar().getKeyStoreInfo(),
                    new Organisasjonsnummer(avsender.getOrganisasjonsnummer()),
                    digipostMeldingsformidler)
                    .build();
        } catch (Exception e) {
            // TODO: Either throw something more specific from MessageSender or wrap in relevant exception
            throw new RuntimeException("Could not create MessageSender", e);
        }
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
            throw new UnsupportedOperationException("Fysiske forsendelser er ikke implementert");
        }

        EbmsForsendelse ebmsForsendelse = ebmsForsendelseBuilder.buildEbmsForsendelse(avsender, forsendelse);
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
        EbmsPullRequest ebmsPullRequest = kvitteringBuilder.buildEbmsPullRequest(digipostMeldingsformidler, kvitteringForespoersel.getPrioritet());

        EbmsApplikasjonsKvittering applikasjonsKvittering = messageSender.hentKvittering(ebmsPullRequest);

        if (applikasjonsKvittering != null) {
            return kvitteringBuilder.buildForretningsKvittering(applikasjonsKvittering);
        }
        return null;
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
    public void bekreftKvittering(BekreftelsesKvittering bekreftelsesKvittering) {
        EbmsApplikasjonsKvittering kvittering = kvitteringBuilder.buildEbmsApplikasjonsKvittering(bekreftelsesKvittering);
        messageSender.bekreft(kvittering);
    }
}
