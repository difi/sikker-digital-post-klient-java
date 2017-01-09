package no.difi.sdp.client2.internal;

import no.digipost.security.cert.Trust;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class TrustedCertificatesTest {

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