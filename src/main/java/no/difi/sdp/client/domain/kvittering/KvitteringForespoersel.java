package no.difi.sdp.client.domain.kvittering;

import no.difi.sdp.client.domain.Prioritet;

public class KvitteringForespoersel {

    private KvitteringForespoersel(Prioritet prioritet) {
        this.prioritet = prioritet;
    }

    private Prioritet prioritet;

    public Prioritet getPrioritet() {
        return prioritet;
    }

    /**
     * @param prioritet Hvilken prioritet det forespørres kvittering for. De ulike prioritene kan ses på som egne køer for kvitteringer. Dersom en forsendelse er sendt med normal prioritet, vil den kun dukke opp dersom det spørres om kvittering på normal prioritet.
     */
    public static Builder builder(Prioritet prioritet) {
        return new Builder(prioritet);
    }

    public static class Builder {
        private final KvitteringForespoersel target;
        private boolean built = false;

        private Builder(Prioritet prioritet) {
            target = new KvitteringForespoersel(prioritet);
        }

        public KvitteringForespoersel build() {
            if (built) throw new IllegalStateException("Can't build twice");
            built = true;
            return target;
        }
    }
}
