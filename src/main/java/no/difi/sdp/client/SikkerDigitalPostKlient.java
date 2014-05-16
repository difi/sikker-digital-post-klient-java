package no.difi.sdp.client;

import no.difi.sdp.client.domain.*;

public class SikkerDigitalPostKlient {

    public SikkerDigitalPostKlient(Avsender avsender, KlientKonfigurasjon konfigurasjon) {
    }

    /**
     * Sender en forsendelse til meldingsformidler. En forsendelse kan være digital post eller fysisk post.
     * Dersom noe feilet i sendingen til meldingsformidler, vil det kastes en exception med beskrivende feilmelding.
     *
     * @param forsendelse Et objekt som har all informasjon klar til å kunne sendes (mottakerinformasjon, sertifikater, dokumenter mm),
     *                    enten digitalt eller fyisk.
     */
    public void send(Forsendelse forsendelse) {
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
        return new ForretningsKvittering();
    }
}
