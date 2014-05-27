package no.difi.sdp.client.domain;

import no.difi.sdp.client.domain.exceptions.NoekkelException;
import no.posten.dpost.offentlig.api.interceptors.KeyStoreInfo;

import java.security.*;

public class Noekkelpar {

    private Noekkelpar(KeyStore keyStore, String alias, String password) {
        this.keyStore = keyStore;
        this.alias = alias;
        this.password = password;
    }

    private KeyStore keyStore;
    private String alias;
    private String password;

    public KeyStore getKeyStore() {
        return keyStore;
    }

    public KeyStoreInfo getKeyStoreInfo() {
        return new KeyStoreInfo(keyStore, alias, password);
    }

    public static Noekkelpar fraKeyStore(KeyStore keyStore, String alias, String password) {
        return new Noekkelpar(keyStore, alias, password);
    }

    public Sertifikat getSertifikat() {
        return Sertifikat.fraKeyStore(keyStore, alias);
    }

    public PrivateKey getPrivateKey() {
        try {
            Key key = keyStore.getKey(alias, password.toCharArray());
            if (!(key instanceof PrivateKey)) {
                throw new NoekkelException("Kunne ikke hente privat nøkkel fra key store. Forventet å få en PrivateKey, fikk " + key.getClass().getCanonicalName());
            }
            return (PrivateKey) key;
        } catch (KeyStoreException e) {
            throw new NoekkelException("Kunne ikke hente privat nøkkel fra KeyStore. Er KeyStore initialisiert?", e);
        } catch (NoSuchAlgorithmException e) {
            throw new NoekkelException("Kunne ikke hente privat nøkkel fra KeyStore. Verifiser at nøkkelen er støttet på plattformen", e);
        } catch (UnrecoverableKeyException e) {
            throw new NoekkelException("Kunne ikke hente privat nøkkel fra KeyStore. Sjekk at passordet er riktig.", e);
        }
    }

    public String getAlias() {
        return alias;
    }
}
