package no.difi.sdp.client2.domain;

import no.difi.sdp.client2.internal.Environment;
import no.difi.sdp.client2.internal.TrustedCertificates;
import no.digipost.security.cert.Trust;

import java.net.URI;

public class Miljo {

    public static Miljo PRODUKSJON = new Miljo(Environment.PRODUCTION, URI.create("https://meldingsformidler.digipost.no/api/"));
    public static Miljo PRODUKSJON_NORSK_HELSENETT = new Miljo(Environment.PRODUCTION, URI.create("https://meldingsformidler.nhn.digipost.no:4444/api/"));
    public static Miljo FUNKSJONELT_TESTMILJO = new Miljo(Environment.TEST, URI.create("https://qaoffentlig.meldingsformidler.digipost.no/api/"));
    public static Miljo FUNKSJONELT_TESTMILJO_NORSK_HELSENETT = new Miljo(Environment.TEST, URI.create("https://qaoffentlig.meldingsformidler.nhn.digipost.no:4445/api/"));

    Trust godkjenteKjedeSertifikater;
    URI meldingsformidlerRoot;

    private Miljo(Environment environment, URI meldingsformidlerRoot) {
        this(TrustedCertificates.createTrust(environment), meldingsformidlerRoot);
    }

    public Miljo(Trust godkjenteKjedeSertifikater, URI meldingsformidlerRoot) {
        this.godkjenteKjedeSertifikater = godkjenteKjedeSertifikater;
        this.meldingsformidlerRoot = meldingsformidlerRoot;
    }

    public Trust getGodkjenteKjedeSertifikater() {
        return godkjenteKjedeSertifikater;
    }

    public void setGodkjenteKjedeSertifikater(Trust godkjenteKjedeSertifikater) {
        this.godkjenteKjedeSertifikater = godkjenteKjedeSertifikater;
    }

    public URI getMeldingsformidlerRoot() {
        return meldingsformidlerRoot;
    }

    public void setMeldingsformidlerRoot(URI meldingsformidlerRoot) {
        this.meldingsformidlerRoot = meldingsformidlerRoot;
    }
}
