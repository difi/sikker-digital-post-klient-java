package no.difi.sdp.client.domain.kvittering;

import java.util.Date;

public class AapningsKvittering extends ForretningsKvittering {

    private AapningsKvittering(Date tidspunkt, String konversasjonsId, String refToMessageId) {
        super(tidspunkt, konversasjonsId, refToMessageId);
    }

    public static Builder builder(Date tidspunkt, String konversasjonsId, String refToMessageId) {
        return new Builder(tidspunkt, konversasjonsId, refToMessageId);
    }

    public static class Builder {
        private AapningsKvittering target;
        private boolean built = false;

        public Builder(Date tidspunkt, String konversasjonsId, String refToMessageId) {
            target = new AapningsKvittering(tidspunkt, konversasjonsId, refToMessageId);
        }

        public AapningsKvittering build() {
            if (built) throw new IllegalStateException("Can't build twice");
            built = true;
            return target;
        }
    }
}
