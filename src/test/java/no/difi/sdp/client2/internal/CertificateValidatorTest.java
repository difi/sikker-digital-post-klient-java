package no.difi.sdp.client2.internal;

import no.difi.sdp.client2.domain.Miljo;
import no.difi.sdp.client2.domain.exceptions.SertifikatException;
import no.digipost.security.DigipostSecurity;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.security.cert.X509Certificate;

import static org.junit.Assert.assertEquals;

public class CertificateValidatorTest {

    public static final X509Certificate POSTEN_TEST_CERTIFICATE = DigipostSecurity.readCertificate("certificates/test/posten_test.pem");
    public static final X509Certificate POSTEN_PROD_CERTIFICATE = DigipostSecurity.readCertificate("certificates/prod/posten_prod.pem");

    @Test
    public void accepts_test_certificate() {
        CertificateValidator.Validate(Miljo.FUNKSJONELT_TESTMILJO, POSTEN_TEST_CERTIFICATE);
    }

    @Test
    public void accepts_prod_certificate() {
        CertificateValidator.Validate(Miljo.PRODUKSJON, POSTEN_PROD_CERTIFICATE);
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void stops_test_certificate_in_prod() {
        thrown.expect(SertifikatException.class);
        CertificateValidator.Validate(Miljo.PRODUKSJON, POSTEN_TEST_CERTIFICATE);
    }

    @Test
    public void stops_prod_certificate_in_test() {
        thrown.expect(SertifikatException.class);
        CertificateValidator.Validate(Miljo.FUNKSJONELT_TESTMILJO, POSTEN_PROD_CERTIFICATE);
    }

    @Test
    public void no_validation_if_environment_is_null(){
        CertificateValidator.Validate(null, POSTEN_PROD_CERTIFICATE);
    }
}