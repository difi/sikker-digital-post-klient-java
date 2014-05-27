package no.difi.sdp.client.domain.kvittering;

import java.util.Date;

public class LeveringsKvittering extends ForretningsKvittering {

    private LeveringsKvittering(Date tidspunkt, String konversasjonsId, String refToMessageId) {
        super(tidspunkt, konversasjonsId, refToMessageId);
    }

    public static Builder builder(Date tidspunkt, String konversasjonsId, String refToMessageId) {
        return new Builder(tidspunkt, konversasjonsId, refToMessageId);
    }

    public static class Builder {
        private LeveringsKvittering target;
        private boolean built = false;

        public Builder(Date tidspunkt, String konversasjonsId, String refToMessageId) {
            target = new LeveringsKvittering(tidspunkt, konversasjonsId, refToMessageId);
        }

        public LeveringsKvittering build() {
            if (built) throw new IllegalStateException("Can't build twice");
            built = true;
            return target;
        }
    }
}
