package no.difi.sdp.client.domain.kvittering;

import java.util.Date;

public class LeveringsKvittering extends ForretningsKvittering {

    public LeveringsKvittering(Date tidspunkt, String konversasjonsId) {
        super(tidspunkt, konversasjonsId);
    }

    public static Builder builder(Date tidspunkt, String konversasjonsId) {
        return new Builder(tidspunkt, konversasjonsId);
    }

    public static class Builder {
        private LeveringsKvittering target;
        private boolean built = false;

        public Builder(Date tidspunkt, String konversasjonsId) {
            target = new LeveringsKvittering(tidspunkt, konversasjonsId);
        }

        public LeveringsKvittering build() {
            if (built) throw new IllegalStateException("Can't build twice");
            built = true;
            return target;
        }
    }
}
