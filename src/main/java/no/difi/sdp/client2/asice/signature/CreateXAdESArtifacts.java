package no.difi.sdp.client2.asice.signature;

import no.difi.sdp.client2.asice.AsicEAttachable;
import no.difi.sdp.client2.domain.Sertifikat;
import org.etsi.uri._01903.v1_3.CertIDType;
import org.etsi.uri._01903.v1_3.DataObjectFormat;
import org.etsi.uri._01903.v1_3.DigestAlgAndValueType;
import org.etsi.uri._01903.v1_3.QualifyingProperties;
import org.etsi.uri._01903.v1_3.SignedDataObjectProperties;
import org.etsi.uri._01903.v1_3.SignedProperties;
import org.etsi.uri._01903.v1_3.SignedSignatureProperties;
import org.etsi.uri._01903.v1_3.SigningCertificate;
import org.w3.xmldsig.X509IssuerSerialType;

import javax.xml.crypto.dsig.DigestMethod;

import java.security.cert.X509Certificate;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.apache.commons.codec.digest.DigestUtils.sha1;

class CreateXAdESArtifacts {

    private static final org.w3.xmldsig.DigestMethod sha1DigestMethod = new org.w3.xmldsig.DigestMethod(emptyList(), DigestMethod.SHA1);
    private final Clock clock;


    CreateXAdESArtifacts(Clock clock) {
        this.clock = clock;
    }

    XAdESArtifacts createArtifactsToSign(List<AsicEAttachable> files, Sertifikat sertifikat) {
        byte[] certificateDigestValue = sha1(sertifikat.getEncoded());
        X509Certificate certificate = sertifikat.getX509Certificate();

        DigestAlgAndValueType certificateDigest = new DigestAlgAndValueType(sha1DigestMethod, certificateDigestValue);
        X509IssuerSerialType certificateIssuer = new X509IssuerSerialType(certificate.getIssuerDN().getName(), certificate.getSerialNumber());
        SigningCertificate signingCertificate = new SigningCertificate(singletonList(new CertIDType(certificateDigest, certificateIssuer, null)));

        ZonedDateTime now = ZonedDateTime.now(clock);
        SignedSignatureProperties signedSignatureProperties = new SignedSignatureProperties().withSigningTime(now).withSigningCertificate(signingCertificate);
        SignedDataObjectProperties signedDataObjectProperties = new SignedDataObjectProperties().withDataObjectFormats(dataObjectFormats(files));
        SignedProperties signedProperties = new SignedProperties(signedSignatureProperties, signedDataObjectProperties, "SignedProperties");
        QualifyingProperties qualifyingProperties = new QualifyingProperties().withSignedProperties(signedProperties).withTarget("#Signature");

        return XAdESArtifacts.from(qualifyingProperties);
    }

    private static List<DataObjectFormat> dataObjectFormats(List<AsicEAttachable> files) {
        List<DataObjectFormat> result = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            String signatureElementIdReference = "#ID_" + i;
            result.add(new DataObjectFormat(null, null, files.get(i).getMimeType(), null, signatureElementIdReference));
        }
        return result;
    }

}