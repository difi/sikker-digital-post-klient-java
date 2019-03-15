package no.difi.sdp.client2.internal;

import no.digipost.security.cert.Trust;
import org.junit.jupiter.api.Test;

import java.security.KeyStore;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class TrustedCertificatesTest {
    @Test
    public void trust_store_for_production_and_test() throws Exception {
        int numOfEnvironments = 2;
        int numOfRootCerts = 2;
        int numOfIntermediateCerts = 2;
        int numOfCertificates = numOfEnvironments * (numOfRootCerts + numOfIntermediateCerts);

        KeyStore trustStore = TrustedCertificates.getTrustStore();

        assertThat(trustStore.size(), equalTo(numOfCertificates));
    }

    @Test
    public void with_prod_certificates() {
        hasCorrectCertificateCount(Environment.PRODUCTION);
    }

    @Test
    public void with_test_certificates() {
        hasCorrectCertificateCount(Environment.TEST);
    }

    private void hasCorrectCertificateCount(Environment environment) {
        int numOfRootCerts = 2;
        int numOfIntermediateCerts = 2;
        Trust trustedCerts = TrustedCertificates.createTrust(environment);
        assertThat(trustedCerts.getTrustAnchors().size(), equalTo(numOfRootCerts));
        assertThat(trustedCerts.getTrustedIntermediateCertificates().size(), equalTo(numOfIntermediateCerts));
    }
}