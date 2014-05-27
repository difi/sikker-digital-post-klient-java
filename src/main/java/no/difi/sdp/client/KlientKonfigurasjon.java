package no.difi.sdp.client;

import java.net.URI;

public class KlientKonfigurasjon {

    private URI meldingsformidlerRoot = URI.create("https://meldingsformidler.digipost.no");

    private KlientKonfigurasjon() {}

    public URI getMeldingsformidlerRoot() {
        return meldingsformidlerRoot;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final KlientKonfigurasjon target;

        private Builder() {
            target = new KlientKonfigurasjon();
        }

        public Builder meldingsformidlerRoot(String meldingsformidlerRoot) {
            target.meldingsformidlerRoot = URI.create(meldingsformidlerRoot);
            return this;
        }

        public KlientKonfigurasjon build() {
            return target;
        }
    }
}
