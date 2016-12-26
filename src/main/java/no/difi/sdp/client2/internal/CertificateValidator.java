package no.difi.sdp.client2.internal;

import no.difi.sdp.client2.domain.Miljo;
import no.digipost.security.cert.CertStatus;
import no.digipost.security.cert.CertificateValidatorConfig;
import no.digipost.security.cert.Trust;
import org.apache.http.impl.client.CloseableHttpClient;

import java.security.cert.X509Certificate;

import static no.digipost.security.cert.OcspSetting.NO_OCSP;

public class CertificateValidator {

    public static CertStatus Validate(Miljo miljo, X509Certificate certificate) {
        Trust trusteChainCertificates = miljo.getGodkjenteKjedeSertifikater();

        CertificateValidatorConfig certificateValidatorConfig = CertificateValidatorConfig.MOST_STRICT.with(NO_OCSP);
        no.digipost.security.cert.CertificateValidator certificateValidator = new no.digipost.security.cert.CertificateValidator(certificateValidatorConfig, trusteChainCertificates, null);

        return certificateValidator.validateCert(certificate);
    }
}
