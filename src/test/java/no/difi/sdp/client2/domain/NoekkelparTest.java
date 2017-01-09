package no.difi.sdp.client2.domain;

import no.difi.sdp.client2.ObjectMother;
import org.junit.Test;

import java.security.KeyStore;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;


public class NoekkelparTest {

    @Test
    public void initializes_from_key_store() throws Exception {
        KeyStore keyStoreMedVirksomhetssertifikatOgTrustStore = testKeyStore();
        Noekkelpar noekkelpar = Noekkelpar.fraKeyStore(keyStoreMedVirksomhetssertifikatOgTrustStore, ObjectMother.VIRKSOMHETSSERTIFIKAT_ALIAS, ObjectMother.VIRKSOMHETSSERTIFIKAT_PASSORD);

        assertThat(noekkelpar.getKeyStore(), equalTo(keyStoreMedVirksomhetssertifikatOgTrustStore));
        assertThat(noekkelpar.getTrustStore(), is(nullValue()));
    }

    @Test
    public void initializes_from_key_store_without_trust_store() throws Exception {
        KeyStore keyStoreUtenTrustStore = testKeyStore();
        Noekkelpar noekkelpar = Noekkelpar.fraKeyStoreUtenTrustStore(keyStoreUtenTrustStore, ObjectMother.VIRKSOMHETSSERTIFIKAT_ALIAS, ObjectMother.VIRKSOMHETSSERTIFIKAT_PASSORD);

        assertThat(noekkelpar.getKeyStore(), equalTo(keyStoreUtenTrustStore));
        assertThat(noekkelpar.getTrustStore(), notNullValue());
        assertThat(noekkelpar.getTrustStore(), not(equalTo((noekkelpar.getKeyStore()))));
    }

    @Test
    public void initializes_from_key_store_and_trust_store() throws Exception {
        KeyStore keyStoreMedVirksomhetssertifikat = testKeyStore();
        KeyStore trustStore = testKeyStore();

        Noekkelpar noekkelpar = Noekkelpar.fraKeyStoreOgTrustStore(keyStoreMedVirksomhetssertifikat, trustStore, ObjectMother.VIRKSOMHETSSERTIFIKAT_ALIAS, ObjectMother.VIRKSOMHETSSERTIFIKAT_PASSORD);

        assertThat(noekkelpar.getKeyStore(), equalTo(keyStoreMedVirksomhetssertifikat));
        assertThat(noekkelpar.getTrustStore(), equalTo(trustStore));
    }

    private static KeyStore testKeyStore() {
        return ObjectMother.selvsignertKeyStore();
    }

}