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
    private String virksomhetssertifikatPassord;

    private static final String TRUST_STORE_PASSORD = "sophisticatedpassword";
    private static final String TRUST_STORE_STI = "/TrustStore.jceks";

    private Noekkelpar(KeyStore keyStore, KeyStore trustStore, String virksomhetssertifikatAlias, String virksomhetssertifikatPassord) {
        this(keyStore, virksomhetssertifikatAlias, virksomhetssertifikatPassord);
        this.trustStore = trustStore;
    }

    private Noekkelpar(KeyStore keyStore, String virksomhetssertifikatAlias, String virksomhetssertifikatPassord) {
        this.keyStore = keyStore;
        this.virksomhetssertifikatAlias = virksomhetssertifikatAlias;
        this.virksomhetssertifikatPassord = virksomhetssertifikatPassord;
    }

    public String getAlias() {
        return virksomhetssertifikatAlias;
    }

    public KeyStore getKeyStore() {
        return keyStore;
    }

    public KeyStore getTrustStore() { return trustStore; }

    public KeyStoreInfo getKeyStoreInfo() {
        if(trustStore != null)
        {
            return new KeyStoreInfo(keyStore, trustStore, virksomhetssertifikatAlias, virksomhetssertifikatPassord);
        }

        return new KeyStoreInfo(keyStore, virksomhetssertifikatAlias, virksomhetssertifikatPassord);
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
            Key key = keyStore.getKey(virksomhetssertifikatAlias, virksomhetssertifikatPassord.toCharArray());
            if (!(key instanceof PrivateKey)) {
                throw new NoekkelException("Kunne ikke hente privat nøkkel fra KeyStore. Forventet å få en PrivateKey, fikk " + key.getClass().getCanonicalName());
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

    public static Noekkelpar fraKeyStore(KeyStore keyStore, String virksomhetssertifikatAlias, String virksomhetssertifikatPassord) {
        return new Noekkelpar(keyStore, virksomhetssertifikatAlias, virksomhetssertifikatPassord);
    }

    public static Noekkelpar fraKeyStoreUtenTrustStore(KeyStore keyStore, String virksomhetssertifikatAlias, String virksomhetssertifikatPassord) {
        return new Noekkelpar(keyStore, getStandardTrustStore(), virksomhetssertifikatAlias, virksomhetssertifikatPassord);
    }

    private static KeyStore getStandardTrustStore() {
        try {
            KeyStore trustStore = KeyStore.getInstance("JCEKS");
            trustStore.load(new ClassPathResource(TRUST_STORE_STI).getInputStream(), TRUST_STORE_PASSORD.toCharArray());
            return trustStore;
        }
        catch (Exception e) {
            throw new NoekkelException(String.format("Kunne ikke initiere trust store. Fant ikke '%s'", TRUST_STORE_STI), e);
        }
    }

    public static Noekkelpar fraKeyStoreOgTrustStore(KeyStore keyStore, KeyStore trustStore, String virksomhetssertifikatAlias, String virksomhetssertifikatPassword) {
        return new Noekkelpar(keyStore, trustStore, virksomhetssertifikatAlias, virksomhetssertifikatPassword);
    }
}
