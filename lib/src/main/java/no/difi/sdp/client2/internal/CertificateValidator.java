package no.difi.sdp.client2.internal;

import no.difi.sdp.client2.domain.Databehandler;
import no.difi.sdp.client2.domain.Miljo;
import no.difi.sdp.client2.domain.exceptions.SertifikatException;
import no.digipost.security.cert.CertStatus;
import no.digipost.security.cert.CertificateValidatorConfig;
import no.digipost.security.cert.Trust;

import java.security.cert.X509Certificate;
import java.text.MessageFormat;

import static no.digipost.security.cert.OcspPolicy.NEVER_DO_OCSP_LOOKUP;

public class CertificateValidator {

    public static void validate(Miljo miljo, X509Certificate certificate) {
        if (miljo.getGodkjenteKjedeSertifikater() == null) {
            return;
        }

        Trust trusteChainCertificates = miljo.getGodkjenteKjedeSertifikater();

        CertificateValidatorConfig certificateValidatorConfig = CertificateValidatorConfig.MOST_STRICT.withOcspPolicy(NEVER_DO_OCSP_LOOKUP);
        no.digipost.security.cert.CertificateValidator certificateValidator = new no.digipost.security.cert.CertificateValidator(certificateValidatorConfig, trusteChainCertificates, null);

        CertStatus certStatus = certificateValidator.validateCert(certificate);

        if (certStatus != CertStatus.OK) {
            String message = MessageFormat
                    .format("Sertifikatet som brukes for {0} er ikke gyldig. Prøver du å sende med et testsertifikat i produksjonsmiljø eller omvendt, eller er sertifikatet utgått?", Databehandler.class.getSimpleName());
            throw new SertifikatException(message);
        }
    }
}
