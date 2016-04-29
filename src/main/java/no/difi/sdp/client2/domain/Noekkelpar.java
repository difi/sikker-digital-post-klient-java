/**
 * Copyright (C) Posten Norge AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package no.difi.sdp.client2.domain;

import no.difi.sdp.client2.domain.exceptions.NoekkelException;
import no.digipost.api.interceptors.KeyStoreInfo;

import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;

public class Noekkelpar {

    private KeyStore keyStore;
    private String alias;
    private String password;

    private Noekkelpar(KeyStore keyStore, String alias, String password) {
        this.keyStore = keyStore;
        this.alias = alias;
        this.password = password;
    }

    public String getAlias() {
        return alias;
    }

    public KeyStore getKeyStore() {
        return keyStore;
    }

    public KeyStoreInfo getKeyStoreInfo() {
        return new KeyStoreInfo(keyStore, alias, password);
    }

    public Sertifikat getSertifikat() {
        return Sertifikat.fraKeyStore(keyStore, alias);
    }

    public Certificate[] getCertificateChain() {
        try {
            return keyStore.getCertificateChain(alias);
        } catch (KeyStoreException e) {
            throw new NoekkelException("Kunne ikke hente privat nøkkel fra KeyStore. Er KeyStore initialisiert?", e);
        }
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

    public static Noekkelpar fraKeyStore(KeyStore keyStore, String alias, String password) {
        return new Noekkelpar(keyStore, alias, password);
    }
}
