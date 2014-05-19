package no.difi.sdp.client.domain;

import java.util.Date;

public class TilbaketrekkingsKvittering extends ForretningsKvittering {

    private String beskrivelse;
    private TilbaketrekkingsStatus status;

    public TilbaketrekkingsKvittering(Date tidspunkt, String konversasjonsId, TilbaketrekkingsStatus status) {
        super(tidspunkt, konversasjonsId);
        this.status = status;
    }

    public static Builder builder(Date tidspunkt, String konverasjonsId, TilbaketrekkingsStatus status) {
        return new Builder(tidspunkt, konverasjonsId, status);
    }

    public static class Builder {
        private TilbaketrekkingsKvittering target;
        private boolean built = false;

        public Builder(Date tidspunkt, String konversasjonsId, TilbaketrekkingsStatus status) {
            target = new TilbaketrekkingsKvittering(tidspunkt, konversasjonsId, status);
        }

        public Builder status(TilbaketrekkingsStatus status) {
            target.status = status;
            return this;
        }

        public Builder beskrivelse(String beskrivelse) {
            target.beskrivelse = beskrivelse;
            return this;
        }

        public TilbaketrekkingsKvittering build() {
            if (built) throw new IllegalStateException("Can't build twice");
            built = true;
            return target;
        }
    }
}
