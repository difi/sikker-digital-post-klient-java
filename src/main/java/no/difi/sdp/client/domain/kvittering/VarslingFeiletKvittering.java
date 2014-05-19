package no.difi.sdp.client.domain.kvittering;

import java.util.Date;

public class VarslingFeiletKvittering extends ForretningsKvittering {

    private Varslingskanal varslingskanal;
    private String beskrivelse;

    public VarslingFeiletKvittering(Date tidspunkt, String konversasjonsId, Varslingskanal varslingskanal) {
        super(tidspunkt, konversasjonsId);
        this.varslingskanal = varslingskanal;
    }

    public static Builder builder(Date tidspunkt, String konversasjonsId, Varslingskanal varslingskanal) {
        return new Builder(tidspunkt, konversasjonsId, varslingskanal);
    }

    public static class Builder {
        private VarslingFeiletKvittering target;
        private boolean built = false;

        public Builder(Date tidspunkt, String konversasjonsId, Varslingskanal varslingskanal) {
            target = new VarslingFeiletKvittering(tidspunkt, konversasjonsId, varslingskanal);
        }

        public Builder feilbeskrivelse(String feilbeskrivelse) {
            target.beskrivelse = feilbeskrivelse;
            return this;
        }

        public VarslingFeiletKvittering build() {
            if (built) throw new IllegalStateException("Can't build twice");
            built = true;
            return target;
        }
    }
}
