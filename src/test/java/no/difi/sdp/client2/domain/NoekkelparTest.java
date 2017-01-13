package no.difi.sdp.client2.domain;

import no.difi.sdp.client2.ObjectMother;
import no.difi.sdp.client2.domain.exceptions.SertifikatException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.security.KeyStore;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;


public class NoekkelparTest {

    public static class fraKeyStoreMethod {

        @Test
        public void initializes_from_key_store() {
            KeyStore keyStoreMedVirksomhetssertifikatOgTrustStore = testKeyStore();
            Noekkelpar noekkelpar = Noekkelpar.fraKeyStore(keyStoreMedVirksomhetssertifikatOgTrustStore, ObjectMother.SELVSIGNERT_VIRKSOMHETSSERTIFIKAT_ALIAS, ObjectMother.SELVSIGNERT_VIRKSOMHETSSERTIFIKAT_PASSORD);

            assertThat(noekkelpar.getKeyStore(), equalTo(keyStoreMedVirksomhetssertifikatOgTrustStore));
            assertThat(noekkelpar.getTrustStore(), is(nullValue()));
        }

        @Test
        public void can_disable_key_store_validation(){
            KeyStore keyStore = NoekkelparTest.testKeyStore();

            Noekkelpar.AKTIV_KEY_STORE_VALIDERING = false;
            Noekkelpar noekkelpar = Noekkelpar.fraKeyStore(keyStore, ObjectMother.SELVSIGNERT_VIRKSOMHETSSERTIFIKAT_ALIAS, ObjectMother.SELVSIGNERT_VIRKSOMHETSSERTIFIKAT_PASSORD);
        }
    }

    public static class fraKeyStoreUtenTrustStoreMethod {

        @Test
        public void initializes_from_key_store_without_trust_store() {
            KeyStore keyStoreUtenTrustStore = testKeyStore();
            Noekkelpar noekkelpar = Noekkelpar.fraKeyStoreUtenTrustStore(keyStoreUtenTrustStore, ObjectMother.SELVSIGNERT_VIRKSOMHETSSERTIFIKAT_ALIAS, ObjectMother.SELVSIGNERT_VIRKSOMHETSSERTIFIKAT_PASSORD);

            assertThat(noekkelpar.getKeyStore(), equalTo(keyStoreUtenTrustStore));
            assertThat(noekkelpar.getTrustStore(), notNullValue());
            assertThat(noekkelpar.getTrustStore(), not(equalTo((noekkelpar.getKeyStore()))));
        }
    }

    public static class fraKeyStoreOgTrustStoreMethod {

        @Rule
        public final ExpectedException thrown = ExpectedException.none();

        @Test
        public void initializes_from_key_store_and_trust_store() {
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

            thrown.expect(SertifikatException.class);
            Noekkelpar.fraKeyStoreOgTrustStore(keyStore, trustStore, ObjectMother.SELVSIGNERT_VIRKSOMHETSSERTIFIKAT_ALIAS, ObjectMother.SELVSIGNERT_VIRKSOMHETSSERTIFIKAT_PASSORD);
        }

        @Test
        public void can_disable_trust_store_validation() {
            KeyStore keyStore = NoekkelparTest.testKeyStore();
            KeyStore trustStore = NoekkelparTest.testKeyStore();

            Noekkelpar.AKTIV_TRUST_STORE_VALIDERING = false;
            Noekkelpar noekkelpar = Noekkelpar.fraKeyStoreOgTrustStore(keyStore, trustStore, ObjectMother.SELVSIGNERT_VIRKSOMHETSSERTIFIKAT_ALIAS, ObjectMother.SELVSIGNERT_VIRKSOMHETSSERTIFIKAT_PASSORD);
        }
    }

    private static KeyStore testKeyStore() {
        return ObjectMother.selvsignertKeyStore();
    }

}