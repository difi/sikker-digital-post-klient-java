package no.difi.sdp.client.domain;

import java.util.Date;

public class AapningsKvittering extends ForretningsKvittering {

    public AapningsKvittering(Date tidspunkt, String konversasjonsId) {
        super(tidspunkt, konversasjonsId);
    }

    public static Builder builder(Date tidspunkt, String konversasjonsId) {
        return new Builder(tidspunkt, konversasjonsId);
    }

    public static class Builder {
        private AapningsKvittering target;
        private boolean built = false;

        public Builder(Date tidspunkt, String konversasjonsId) {
            target = new AapningsKvittering(tidspunkt, konversasjonsId);
        }

        public AapningsKvittering build() {
            if (built) throw new IllegalStateException("Can't build twice");
            built = true;
            return target;
        }
    }
}
