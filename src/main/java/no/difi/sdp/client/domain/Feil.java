package no.difi.sdp.client.domain;

import no.difi.sdp.client.domain.kvittering.ForretningsKvittering;

import java.util.Date;

public class Feil extends ForretningsKvittering {

    private Feiltype feiltype;
    private String detaljer;

    private Feil(Date tidspunkt, String konversasjonsId, String refToMessageId, Feiltype feiltype) {
        super(tidspunkt, konversasjonsId, refToMessageId);
        this.feiltype = feiltype;
    }

    public Feiltype getFeiltype() {
        return feiltype;
    }

    public String getDetaljer() {
        return detaljer;
    }

    public static Builder builder(Date tidspunkt, String konversasjonsId, String refToMessageId, Feiltype feiltype) {
        return new Builder(tidspunkt, konversasjonsId, refToMessageId, feiltype);
    }

    public static class Builder {
        private Feil target;
        private boolean built = false;

        public Builder(Date tidspunkt, String konversasjonsId, String refToMessageId, Feiltype feiltype) {
            target = new Feil(tidspunkt, konversasjonsId, refToMessageId, feiltype);
        }

        public Builder detaljer(String detaljer) {
            target.detaljer = detaljer;
            return this;
        }

        public Feil build() {
            if (built) throw new IllegalStateException("Can't build twice");
            built = true;
            return target;
        }

    }
}
