package no.difi.sdp.client.domain;

public class KvitteringForespoersel {

    /**
     * Hvilken prioritet det forespørres kvittering for.
     *
     * De ulike prioritene kan ses på som egne køer for kvitteringer. Dersom en forsendelse er sendt med normal prioritet, vil den kun dukke opp dersom det spørres om kvittering på normal prioritet.
     */
    private Prioritet prioritet;

}
