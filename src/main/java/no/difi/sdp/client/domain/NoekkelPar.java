package no.difi.sdp.client.domain;

import no.posten.dpost.offentlig.api.interceptors.KeyStoreInfo;

import java.security.KeyStore;

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

}
