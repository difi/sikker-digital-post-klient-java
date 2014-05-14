package no.difi.sdp.client.domain;

public class KvitteringForespoersel {

    private KvitteringForespoersel(Prioritet prioritet) {
        this.prioritet = prioritet;
    }

    /**
     * Hvilken prioritet det forespørres kvittering for.
     *
     * De ulike prioritene kan ses på som egne køer for kvitteringer. Dersom en forsendelse er sendt med normal prioritet, vil den kun dukke opp dersom det spørres om kvittering på normal prioritet.
     */
    private Prioritet prioritet;

    public static Builder builder(Prioritet prioritet) {
        return new Builder(prioritet);
    }

    public static class Builder {
        private final KvitteringForespoersel target;

        public Builder(Prioritet prioritet) {
            target = new KvitteringForespoersel(prioritet);
        }

        public KvitteringForespoersel build() {
            return target;
        }
    }
}
