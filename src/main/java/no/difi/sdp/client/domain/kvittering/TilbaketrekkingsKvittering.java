package no.difi.sdp.client.domain.kvittering;

import java.util.Date;

public class TilbaketrekkingsKvittering extends ForretningsKvittering {

    private String beskrivelse;
    private TilbaketrekkingsStatus status;

    private TilbaketrekkingsKvittering(Date tidspunkt, String konversasjonsId, String refToMessageId, TilbaketrekkingsStatus status) {
        super(tidspunkt, konversasjonsId, refToMessageId);
        this.status = status;
    }

    public String getBeskrivelse() {
        return beskrivelse;
    }

    public TilbaketrekkingsStatus getStatus() {
        return status;
    }

    public static Builder builder(Date tidspunkt, String konverasjonsId, String refToMessageId, TilbaketrekkingsStatus status) {
        return new Builder(tidspunkt, konverasjonsId, refToMessageId, status);
    }

    public static class Builder {
        private TilbaketrekkingsKvittering target;
        private boolean built = false;

        public Builder(Date tidspunkt, String konversasjonsId, String refToMessageId, TilbaketrekkingsStatus status) {
            target = new TilbaketrekkingsKvittering(tidspunkt, konversasjonsId, refToMessageId, status);
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
