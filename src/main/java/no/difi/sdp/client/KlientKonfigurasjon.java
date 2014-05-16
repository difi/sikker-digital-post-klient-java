package no.difi.sdp.client;

import java.net.URI;

public class KlientKonfigurasjon {

    private URI meldingsformidlerRoot = URI.create("https://meldingsformidler.digipost.no");

    public URI getMeldingsformidlerRoot() {
        return meldingsformidlerRoot;
    }
}
