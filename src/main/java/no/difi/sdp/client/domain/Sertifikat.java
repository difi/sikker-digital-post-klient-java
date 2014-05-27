package no.difi.sdp.client.domain;

import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayInputStream;
import java.security.cert.*;

public class Sertifikat {

    private X509Certificate x509Certificate;

    private Sertifikat(X509Certificate x509Certificate) {
        this.x509Certificate = x509Certificate;
    }

    public X509Certificate getX509Certificate() {
        return x509Certificate;
    }

    public static Sertifikat fraBase64String(String base64) {
        try {
            Certificate x509Certificate = CertificateFactory
                    .getInstance("X509")
                    .generateCertificate(new ByteArrayInputStream(Base64.decodeBase64(base64)));
            return new Sertifikat((X509Certificate) x509Certificate);
        } catch (CertificateException e) {
            throw new UgyldigSertifikatException();
        }
    }

    public static Sertifikat fraX509Certificate(X509Certificate x509Certificate) {
        return new Sertifikat(x509Certificate);
    }

    public static class UgyldigSertifikatException extends RuntimeException {}

    public static class SertifikatException extends RuntimeException {
        public SertifikatException(CertificateEncodingException e) {
            super(e);
        }
    }
}
