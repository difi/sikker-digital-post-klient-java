package no.difi.sdp.client.domain;

public class Varsler {

    private Varsler() { }

    private Varsel epostVarsel;
    private Varsel smsVarsel;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final Varsler target;

        private Builder() {
            this.target = new Varsler();
        }

        public Builder epostVarsel(Varsel epostVarsel) {
            target.epostVarsel = epostVarsel;
            return this;
        }

        public Builder smsVarsel(Varsel smsVarsel) {
            target.smsVarsel = smsVarsel;
            return this;
        }

        public Varsler build() {
            return target;
        }
    }
}
