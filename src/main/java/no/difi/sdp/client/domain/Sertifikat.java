package no.difi.sdp.client.domain;

import no.difi.sdp.client.domain.exceptions.SertifikatException;
import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.*;

public class Sertifikat {

    private X509Certificate certificate;

    private Sertifikat(X509Certificate certificate) {
        this.certificate = certificate;
    }

    public X509Certificate getCertificate() {
        return certificate;
    }

    public byte[] getEncoded() {
        try {
            return certificate.getEncoded();
        } catch (CertificateEncodingException e) {
            throw new SertifikatException("Kunne ikke hente encoded utgave av sertifikatet", e);
        }
    }

    public static Sertifikat fraBase64X509String(String base64) {
        try {
            X509Certificate x509Certificate = (X509Certificate) CertificateFactory
                    .getInstance("X509")
                    .generateCertificate(new ByteArrayInputStream(Base64.decodeBase64(base64)));
            return new Sertifikat(x509Certificate);
        } catch (CertificateException e) {
            throw new SertifikatException("Kunne ikke lese sertifikat fra base64-streng", e);
        }
    }

    public static Sertifikat fraCertificate(X509Certificate certificate) {
        return new Sertifikat(certificate);
    }

    public static Sertifikat fraKeyStore(KeyStore keyStore, String alias) {
        Certificate certificate;
        try {
            certificate = keyStore.getCertificate(alias);
        } catch (KeyStoreException e) {
            throw new SertifikatException("Klarte ikke lese sertifikat fra keystore", e);
        }

        if (certificate == null) {
            throw new SertifikatException("Kunne ikke finne sertifikat i keystore. Er du sikker på at det er brukt keystore med et sertifikat og at du har oppgitt riktig alias?");
        }

        if (!(certificate instanceof X509Certificate)) {
            throw new SertifikatException("Klienten støtter kun X509-sertifikater. Fikk sertifikat av typen " + certificate.getClass().getSimpleName());
        }

        return new Sertifikat((X509Certificate) certificate);
    }
}
