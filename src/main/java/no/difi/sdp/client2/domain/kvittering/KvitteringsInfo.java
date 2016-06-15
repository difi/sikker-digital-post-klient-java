package no.difi.sdp.client2.domain.kvittering;

import java.time.Instant;

public class KvitteringsInfo {

    private String konversasjonsId;
    private String referanseTilMeldingId;
    private Instant tidspunkt;


    public String getKonversasjonsId() {
        return konversasjonsId;
    }

    public String getReferanseTilMeldingId() {
        return referanseTilMeldingId;
    }

    public Instant getTidspunkt() {
        return tidspunkt;
    }

    protected KvitteringsInfo(String konversasjonsId, String referanseTilMeldingId, Instant tidspunkt) {
        this.konversasjonsId = konversasjonsId;
        this.referanseTilMeldingId = referanseTilMeldingId;
        this.tidspunkt = tidspunkt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private boolean built;
        private String konversasjonsId;
        private String referanseTilMeldingId;
        private Instant tidspunkt;

        private Builder() {

        }

        public Builder konversasjonsId(String konversasjonsId) {
            this.konversasjonsId = konversasjonsId;
            return this;
        }

        public Builder referanseTilMeldingId(String referanseTilMeldingId) {
            this.referanseTilMeldingId = referanseTilMeldingId;
            return this;
        }

        public Builder tidspunkt(Instant tidspunkt) {
            this.tidspunkt = tidspunkt;
            return this;
        }

        public KvitteringsInfo build() {
            if (built) {
                throw new IllegalStateException("Kan ikke bygges flere ganger.");
            }

            if (this.tidspunkt == null || this.konversasjonsId == null || this.referanseTilMeldingId == null) {
                throw new RuntimeException("Alle felter må være initialisert for å kunne bygges.");
            }

            built = true;

            return new KvitteringsInfo(konversasjonsId, referanseTilMeldingId, tidspunkt);
        }
    }
}
