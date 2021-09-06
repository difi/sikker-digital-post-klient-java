package no.difi.sdp.client2.domain.kvittering;

import no.difi.sdp.client2.domain.Prioritet;

public class KvitteringForespoersel {

    private Prioritet prioritet;
    private String mpcId;

    private KvitteringForespoersel(Prioritet prioritet) {
        this.prioritet = prioritet;
    }

    public Prioritet getPrioritet() {
        return prioritet;
    }

    /**
     * @param prioritet Hvilken prioritet det forespørres kvittering for. De ulike prioritene kan ses på som egne køer for kvitteringer.
     *                  Dersom en forsendelse er sendt med normal prioritet, vil den kun dukke opp dersom det spørres om kvittering på normal prioritet.
     */
    public static Builder builder(Prioritet prioritet) {
        return new Builder(prioritet);
    }

    public String getMpcId() {
        return mpcId;
    }

    public static class Builder {
        private final KvitteringForespoersel target;
        private boolean built = false;

        private Builder(Prioritet prioritet) {
            target = new KvitteringForespoersel(prioritet);
        }

        /**
         * Brukes til å skille mellom ulike kvitteringskøer for samme tekniske avsender. En forsendelse gjort med en
         * MPC Id vil kun dukke opp i kvitteringskøen med samme MPC Id.
         *
         * Standardverdi er blank MPC Id.
         *
         * @see no.difi.sdp.client2.domain.Forsendelse.Builder#mpcId(String)
         */
        public Builder mpcId(String mpcId) {
            target.mpcId = mpcId;
            return this;
        }

        public KvitteringForespoersel build() {
            if (built) throw new IllegalStateException("Can't build twice");
            built = true;
            return target;
        }
    }
}
