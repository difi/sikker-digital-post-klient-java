package no.difi.sdp.client2.domain;

import no.difi.sdp.client2.domain.exceptions.NoekkelException;
import no.digipost.api.interceptors.KeyStoreInfo;
import org.springframework.core.io.ClassPathResource;

import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;

public class Noekkelpar {

    private KeyStore keyStore;
    private KeyStore trustStore;
    private String virksomhetssertifikatAlias;
    private String virksomhetssertifikatPassword;

    private static final String DEFAULT_TRUST_STORE_PASSWORD = "sophisticatedpassword";
    private static final String DEFAULT_TRUST_STORE_PATH = "/TrustStore.jceks";

    private Noekkelpar(KeyStore keyStore, KeyStore trustStore, String virksomhetssertifikatAlias, String virksomhetssertifikatPassord) {
        this(keyStore, virksomhetssertifikatAlias, virksomhetssertifikatPassord);
        this.trustStore = trustStore;
    }

    private Noekkelpar(KeyStore keyStore, String virksomhetssertifikatAlias, String virksomhetssertifikatPassword) {
        this.keyStore = keyStore;
        this.virksomhetssertifikatAlias = virksomhetssertifikatAlias;
        this.virksomhetssertifikatPassword = virksomhetssertifikatPassword;
    }

    public String getAlias() {
        return virksomhetssertifikatAlias;
    }

    public KeyStore getKeyStore() {
        return keyStore;
    }

    public KeyStore getTrustStore() {
        return trustStore;
    }

    public KeyStoreInfo getKeyStoreInfo() {
        if (trustStore != null) {
            return new KeyStoreInfo(keyStore, trustStore, virksomhetssertifikatAlias, virksomhetssertifikatPassword);
        }

        return new KeyStoreInfo(keyStore, virksomhetssertifikatAlias, virksomhetssertifikatPassword);
    }

    public Sertifikat getVirksomhetssertifikat() {
        return Sertifikat.fraKeyStore(keyStore, virksomhetssertifikatAlias);
    }

    public Certificate[] getVirksomhetssertifikatKjede() {
        try {
            return keyStore.getCertificateChain(virksomhetssertifikatAlias);
        } catch (KeyStoreException e) {
            throw new NoekkelException("Kunne ikke hente privatnøkkel fra KeyStore. Er KeyStore initialisiert?", e);
        }
    }

    public PrivateKey getVirksomhetssertifikatPrivatnøkkel() {
        try {
            Key key = keyStore.getKey(virksomhetssertifikatAlias, virksomhetssertifikatPassword.toCharArray());
            if (!(key instanceof PrivateKey)) {
                throw new NoekkelException("Kunne ikke hente privatnøkkel fra KeyStore. Forventet å få en PrivateKey, fikk " + key.getClass().getCanonicalName());
            }
            return (PrivateKey) key;
        } catch (KeyStoreException e) {
            throw new NoekkelException("Kunne ikke hente privatnøkkel fra KeyStore. Er KeyStore initialisiert?", e);
        } catch (NoSuchAlgorithmException e) {
            throw new NoekkelException("Kunne ikke hente privatnøkkel fra KeyStore. Verifiser at nøkkelen er støttet på plattformen", e);
        } catch (UnrecoverableKeyException e) {
            throw new NoekkelException("Kunne ikke hente privatnøkkel fra KeyStore. Sjekk at passordet er riktig.", e);
        }
    }

    public static Noekkelpar fraKeyStore(KeyStore keyStore, String virksomhetssertifikatAlias, String virksomhetssertifikatPassord) {
        return new Noekkelpar(keyStore, virksomhetssertifikatAlias, virksomhetssertifikatPassord);
    }

    public static Noekkelpar fraKeyStoreUtenTrustStore(KeyStore keyStore, String virksomhetssertifikatAlias, String virksomhetssertifikatPassord) {
        return new Noekkelpar(keyStore, getStandardTrustStore(), virksomhetssertifikatAlias, virksomhetssertifikatPassord);
    }

    private static KeyStore getStandardTrustStore() {
        try {
            KeyStore trustStore = KeyStore.getInstance("JCEKS");
            trustStore.load(new ClassPathResource(DEFAULT_TRUST_STORE_PATH).getInputStream(), DEFAULT_TRUST_STORE_PASSWORD.toCharArray());
            return trustStore;
        } catch (Exception e) {
            throw new NoekkelException(String.format("Kunne ikke initiere trust store. Fant ikke '%s'", DEFAULT_TRUST_STORE_PATH), e);
        }
    }

    public static Noekkelpar fraKeyStoreOgTrustStore(KeyStore keyStore, KeyStore trustStore, String virksomhetssertifikatAlias, String virksomhetssertifikatPassword) {
        return new Noekkelpar(keyStore, trustStore, virksomhetssertifikatAlias, virksomhetssertifikatPassword);
    }
}
