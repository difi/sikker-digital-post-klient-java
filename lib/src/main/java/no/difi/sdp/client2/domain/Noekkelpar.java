/*
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
import no.difi.sdp.client2.domain.exceptions.SertifikatException;
import no.difi.sdp.client2.internal.TrustedCertificates;
import no.digipost.api.interceptors.KeyStoreInfo;

import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.text.MessageFormat;

public class Noekkelpar {

    private KeyStore keyStore;
    private KeyStore trustStore;
    private String virksomhetssertifikatAlias;
    private String virksomhetssertifikatPassword;

    /**
     * For oppretting av {@link Noekkelpar} fra key store og trust store, hvor begge disse er i samme {@link KeyStore}.
     *
     * @param keyStore                     Må inneholde sertifikatkjeden helt opp til rot-CAen for sertifikatutstederen og rotsertifikater som avsenderen stoler på til å identifisere meldingsformidler og postkasser. Oversikt over disse sertifikatene finnes i <a href="http://begrep.difi.no/SikkerDigitalPost/sikkerhet/sertifikathandtering">begrepskatalogen</a>.
     * @param virksomhetssertifikatAlias   Aliaset kan hentes ut fra virksomhetssertifikatet ved å kjøre `keytool -list -keystore VIRKSOMHETSSERTIFIKAT.p12 -storetype pkcs12`. Selve aliaset er siste avsnitt, første del før komma.
     * @param virksomhetssertifikatPassord Dette er passordet som er satt på selve virksomhetssertifikatet.
     */
    public static Noekkelpar fraKeyStore(KeyStore keyStore, String virksomhetssertifikatAlias, String virksomhetssertifikatPassord) {
        return new Noekkelpar(keyStore, virksomhetssertifikatAlias, virksomhetssertifikatPassord, true);
    }

    /**
     * For oppretting av {@link Noekkelpar} fra key store, hvor det er ønskelig å bruke klientens innebygde sertifikater for trust store.
     *
     * @param keyStore                     Må inneholde sertifikatkjeden helt opp til rot-CAen for sertifikatutstederen.
     * @param virksomhetssertifikatAlias   Aliaset kan hentes ut fra virksomhetssertifikatet ved å kjøre `keytool -list -keystore VIRKSOMHETSSERTIFIKAT.p12 -storetype pkcs12`. Selve aliaset er siste avsnitt, første del før komma.
     * @param virksomhetssertifikatPassord Dette er passordet som er satt på selve virksomhetssertifikatet.
     */
    public static Noekkelpar fraKeyStoreUtenTrustStore(KeyStore keyStore, String virksomhetssertifikatAlias, String virksomhetssertifikatPassord) {
        return new Noekkelpar(keyStore, getStandardTrustStore(), virksomhetssertifikatAlias, virksomhetssertifikatPassord, false);
    }

    /**
     * For oppretting av {@link Noekkelpar} fra key store og trust store, hvor hver av disse er separate {@link KeyStore}.
     *
     * @param keyStore                      Må inneholde sertifikatkjeden helt opp til rot-CAen for sertifikatutstederen.
     * @param trustStore                    Rotsertifikater som avsenderen stoler på til å identifisere meldingsformidler og postkasser. Oversikt over disse sertifikatene finnes i <a href="http://begrep.difi.no/SikkerDigitalPost/sikkerhet/sertifikathandtering">begrepskatalogen</a>.
     * @param virksomhetssertifikatAlias    Aliaset kan hentes ut fra virksomhetssertifikatet ved å kjøre `keytool -list -keystore VIRKSOMHETSSERTIFIKAT.p12 -storetype pkcs12`. Selve aliaset er siste avsnitt, første del før komma.
     * @param virksomhetssertifikatPassword Dette er passordet som er satt på selve virksomhetssertifikatet.
     */
    public static Noekkelpar fraKeyStoreOgTrustStore(KeyStore keyStore, KeyStore trustStore, String virksomhetssertifikatAlias, String virksomhetssertifikatPassword) {
        return new Noekkelpar(keyStore, trustStore, virksomhetssertifikatAlias, virksomhetssertifikatPassword, true);
    }

    Noekkelpar(KeyStore keyStore, KeyStore trustStore, String virksomhetssertifikatAlias, String virksomhetssertifikatPassord, boolean withTrustStoreValidation) {
        this(keyStore, virksomhetssertifikatAlias, virksomhetssertifikatPassord, false);
        this.trustStore = trustStore;

        if(withTrustStoreValidation) {
            validateTrustStore(trustStore);
        }
    }

    Noekkelpar(KeyStore keyStore, String virksomhetssertifikatAlias, String virksomhetssertifikatPassword, boolean withKeyStoreValidation) {
        this.keyStore = keyStore;

        if(withKeyStoreValidation){
            validateKeyStore(keyStore);
        }

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

    public PrivateKey getVirksomhetssertifikatPrivatnoekkel() {
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

    private static KeyStore getStandardTrustStore() {
        return TrustedCertificates.getTrustStore();
    }

    //Implementer og override disse valideringe.
    private void validateTrustStore(KeyStore trustStore) {
        try {
            if (trustStore.size() < 4) {
                throw new SertifikatException(MessageFormat.format(
                        "Du initierer {0} med key store og trust store, og da må intermediate- og rotsertifikater til Buypass og Commfides inkluderes" +
                                "i trust store. Et alternativ er å bruke konstruktør som laster innebygd trust store. Dette kan du lese mer om på" +
                                " http://difi.github.io/sikker-digital-post-klient-java.", Noekkelpar.class.getSimpleName()));
            }
        } catch (KeyStoreException e) {
            throw new SertifikatException("Klarte ikke å lese trust store.");
        }
    }

    private void validateKeyStore(KeyStore keyStore) {
        try {
            if (keyStore.size() < 5) {
                throw new SertifikatException(MessageFormat.format(
                        "Du initierer {0} kun med key store, og da må intermediate- og rotsertifikater til Buypass og Commfides inkluderes. " +
                                "Et alternativ er å bruke konstruktør som laster innebygd trust store. Dette kan du lese mer om på" +
                                " http://difi.github.io/sikker-digital-post-klient-java.", Noekkelpar.class.getSimpleName()));
            }
        } catch (KeyStoreException e) {
            throw new SertifikatException("Klarte ikke å lese key store.");
        }
    }
}
