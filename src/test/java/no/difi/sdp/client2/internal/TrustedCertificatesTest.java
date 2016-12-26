package no.difi.sdp.client2.internal;

import no.digipost.security.cert.Trust;
import org.junit.Test;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class TrustedCertificatesTest {

    @Test
    public void with_prod_certificates() {
        hasCorrectCertificateCount(true);
    }

    @Test
    public void with_test_certificates() {
        hasCorrectCertificateCount(false);
    }

    private void hasCorrectCertificateCount(boolean isProduction){
        int numOfRootCerts = 2;
        int numOfIntermediateCerts = 2;
        Trust trustedCerts = TrustedCertificates.createTrust(isProduction);
        assertThat(trustedCerts.getTrustAnchors().size(), equalTo(numOfRootCerts));
        assertThat(trustedCerts.getTrustedIntermediateCertificates().size(), equalTo(numOfIntermediateCerts));
    }
}