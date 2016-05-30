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