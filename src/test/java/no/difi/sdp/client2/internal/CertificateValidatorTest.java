package no.difi.sdp.client2.internal;

import no.difi.sdp.client2.domain.Miljo;
import no.digipost.security.DigipostSecurity;
import no.digipost.security.cert.CertStatus;
import org.junit.Test;

import java.security.cert.X509Certificate;

import static org.junit.Assert.assertEquals;

public class CertificateValidatorTest {

    @Test
    public void accepts_test_certificate() {
        X509Certificate certificate = DigipostSecurity.readCertificate("certificates/test/posten_test.pem");

        CertStatus certStatus = CertificateValidator.Validate(Miljo.FUNKSJONELT_TESTMILJO, certificate);

        assertEquals(CertStatus.OK, certStatus);

    }

    @Test
    public void accepts_prod_certificate() {
        X509Certificate certificate = DigipostSecurity.readCertificate("certificates/prod/posten_prod.pem");

        CertStatus certStatus = CertificateValidator.Validate(Miljo.PRODUKSJON, certificate);

        assertEquals(CertStatus.OK, certStatus);
    }
}