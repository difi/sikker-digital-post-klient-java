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

import no.difi.sdp.client2.ObjectMother;
import org.junit.Test;

import java.security.KeyStore;

import static org.fest.assertions.api.Assertions.assertThat;


public class NoekkelparTest {

    @Test
    public void testFraKeyStore() throws Exception {
        KeyStore keyStoreMedVirksomhetssertifikatOgTrustStore = testKeyStore();
        Noekkelpar noekkelpar = Noekkelpar.fraKeyStore(keyStoreMedVirksomhetssertifikatOgTrustStore, ObjectMother.VIRKSOMHETSSERTIFIKAT_ALIAS, ObjectMother.VIRKSOMHETSSERTIFIKAT_PASSORD);

        assertThat(noekkelpar.getKeyStore()).isEqualTo(keyStoreMedVirksomhetssertifikatOgTrustStore);
        assertThat(noekkelpar.getTrustStore()).isNull();
    }

    @Test
    public void testFraKeyStoreUtenTrustStore() throws Exception {
        KeyStore keyStoreUtenTrustStore = testKeyStore();
        Noekkelpar noekkelpar = Noekkelpar.fraKeyStoreUtenTrustStore(keyStoreUtenTrustStore, ObjectMother.VIRKSOMHETSSERTIFIKAT_ALIAS, ObjectMother.VIRKSOMHETSSERTIFIKAT_PASSORD);

        assertThat(noekkelpar.getKeyStore()).isEqualTo(keyStoreUtenTrustStore);
        assertThat(noekkelpar.getTrustStore()).isNotNull();
        assertThat(noekkelpar.getTrustStore()).isNotEqualTo(noekkelpar.getKeyStore());
    }

    @Test
    public void testFraKeyStoreOgTrustStore() throws Exception {
        KeyStore keyStoreMedVirksomhetssertifikat = testKeyStore();
        KeyStore trustStore = testKeyStore();

        Noekkelpar noekkelpar = Noekkelpar.fraKeyStoreOgTrustStore(keyStoreMedVirksomhetssertifikat, trustStore, ObjectMother.VIRKSOMHETSSERTIFIKAT_ALIAS, ObjectMother.VIRKSOMHETSSERTIFIKAT_PASSORD);

        assertThat(noekkelpar.getKeyStore()).isEqualTo(keyStoreMedVirksomhetssertifikat);
        assertThat(noekkelpar.getTrustStore()).isEqualTo(trustStore);
    }

    private static KeyStore testKeyStore() {
        return ObjectMother.selvsignertKeyStore();
    }

}