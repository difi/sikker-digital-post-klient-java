package no.difi.sdp.client2.domain;

import no.difi.sdp.client2.internal.TrustedCertificates;
import no.digipost.security.cert.Trust;

public class Miljo {

    public static Miljo PROD = new Miljo(true);
    public static Miljo TEST = new Miljo(false);

    Trust godkjenteKjedeSertifikater;

    public Miljo(boolean isProduction) {
        godkjenteKjedeSertifikater = TrustedCertificates.createTrust(isProduction);
    }

    public Trust getGodkjenteKjedeSertifikater() {
        return godkjenteKjedeSertifikater;
    }

    public void setGodkjenteKjedeSertifikater(Trust godkjenteKjedeSertifikater) {
        this.godkjenteKjedeSertifikater = godkjenteKjedeSertifikater;
    }
}
