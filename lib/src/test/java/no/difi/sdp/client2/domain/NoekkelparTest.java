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

import no.difi.sdp.client2.ObjectMother;
import no.difi.sdp.client2.domain.exceptions.SertifikatException;
import org.junit.jupiter.api.Test;

import java.security.KeyStore;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class NoekkelparTest {

    private static KeyStore testKeyStore() {
        return ObjectMother.selvsignertKeyStore();
    }

    public static class fraKeyStoreMethod {

        @Test
        public void initializes() {
            Noekkelpar noekkelpar = ObjectMother.selvsignertNoekkelparUtenTrustStore();

            assertThat(noekkelpar.getKeyStore(), notNullValue());
            assertThat(noekkelpar.getTrustStore(), nullValue());
        }
    }

    public static class fraKeyStoreUtenTrustStoreMethod {

        @Test
        public void initializes_trust_store_different_from_key_store() {
            Noekkelpar noekkelpar = ObjectMother.selvsignertNoekkelparMedTrustStore();

            assertThat(noekkelpar.getKeyStore(), notNullValue());
            assertThat(noekkelpar.getTrustStore(), notNullValue());
            assertThat(noekkelpar.getTrustStore(), not(equalTo((noekkelpar.getKeyStore()))));
        }
    }

    public static class fraKeyStoreOgTrustStoreMethod {

        @Test
        public void initializes() {
            KeyStore keyStoreMedVirksomhetssertifikat = testKeyStore();
            KeyStore trustStore = ObjectMother.testEnvironmentTrustStore();

            Noekkelpar noekkelpar = Noekkelpar.fraKeyStoreOgTrustStore(keyStoreMedVirksomhetssertifikat, trustStore, ObjectMother.SELVSIGNERT_VIRKSOMHETSSERTIFIKAT_ALIAS, ObjectMother.SELVSIGNERT_VIRKSOMHETSSERTIFIKAT_PASSORD);

            assertThat(noekkelpar.getKeyStore(), equalTo(keyStoreMedVirksomhetssertifikat));
            assertThat(noekkelpar.getTrustStore(), equalTo(trustStore));
        }

        @Test
        public void throws_on_length_1_trust_store() {
            KeyStore keyStore = NoekkelparTest.testKeyStore();
            KeyStore trustStore = NoekkelparTest.testKeyStore();

            assertThrows(SertifikatException.class,
                    () -> Noekkelpar.fraKeyStoreOgTrustStore(keyStore, trustStore, ObjectMother.SELVSIGNERT_VIRKSOMHETSSERTIFIKAT_ALIAS, ObjectMother.SELVSIGNERT_VIRKSOMHETSSERTIFIKAT_PASSORD));
        }
    }

}