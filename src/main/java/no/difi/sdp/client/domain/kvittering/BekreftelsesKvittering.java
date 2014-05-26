package no.difi.sdp.client.domain.kvittering;

public class BekreftelsesKvittering {

    private String konversasjonsId;
    private String messsageId;

    private BekreftelsesKvittering(String konversasjonsId, String messsageId) {
        this.konversasjonsId = konversasjonsId;
        this.messsageId = messsageId;
    }

    public static Builder builder(String konversasjonsId, String messageId) {
        return new Builder(konversasjonsId, messageId);
    }

    public static class Builder {
        private BekreftelsesKvittering target;
        private boolean built = false;

        public Builder(String konversasjonsId, String messageId) {
            target = new BekreftelsesKvittering(konversasjonsId, messageId);
        }

        public BekreftelsesKvittering build() {
            if (built) throw new IllegalStateException("Can't build twice");
            built = true;
            return target;
        }
    }
}
