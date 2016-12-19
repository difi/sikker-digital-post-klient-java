package no.difi.sdp.client2.internal;

import no.difi.sdp.client2.domain.Environment;
import no.digipost.security.cert.Trust;
import org.junit.Test;

import static no.difi.sdp.client2.domain.Environment.*;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class TrustedCertificatesTest {

    @Test
    public void with_test_certificates() {
        hasCorrectCertificateCount(TEST);
    }

    @Test
    public void with_prod_certificates() {
        hasCorrectCertificateCount(PROD);
    }

    private void hasCorrectCertificateCount(Environment environment){
        int numOfRootCerts = 2;
        int numOfIntermediateCerts = 2;
        Trust trustedCerts = TrustedCertificates.createTrustFor(environment);
        assertThat(trustedCerts.getTrustAnchors().size(), equalTo(numOfRootCerts));
        assertThat(trustedCerts.getTrustedIntermediateCertificates().size(), equalTo(numOfIntermediateCerts));
    }
}