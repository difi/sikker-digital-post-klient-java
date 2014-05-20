package no.difi.sdp.client.asice;

import no.difi.sdp.client.domain.Avsender;
import no.difi.sdp.client.domain.Forsendelse;
import org.etsi.uri._01903.v1_3.*;
import org.etsi.uri._2918.v1_2.XAdESSignatures;
import org.joda.time.DateTime;
import org.w3.xmldsig.*;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.apache.commons.codec.digest.DigestUtils.sha1;
import static org.apache.commons.codec.digest.DigestUtils.sha256;

public class CreateSignature {

    private final CanonicalizationMethod canonicalizationMethod = new CanonicalizationMethod(emptyList(), "http://www.w3.org/2006/12/xml-c14n11");
    private final SignatureMethod signatureMethod = new SignatureMethod(emptyList(), "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256");
    private final DigestMethod sha1DigestMethod = new DigestMethod(emptyList(), "http://www.w3.org/2000/09/xmldsig#sha1");
    private final DigestMethod sha256DigestMethod = new DigestMethod(emptyList(), "http://www.w3.org/2001/04/xmlenc#sha256");
    private final Transform canonicalXmlTransform = new Transform(emptyList(), "http://www.w3.org/TR/2001/REC-xml-c14n-20010315");
    private final String xadesReferenceType = "http://uri.etsi.org/01903#SignedProperties";

    public XAdESSignatures createSignature(Manifest manifest, Avsender avsender, Forsendelse forsendelse) {
        List<AsicEAttachable> files = new ArrayList<AsicEAttachable>();

        // Lag signatur-referanse for alle filer (hoveddokument, vedlegg og manifestet)
        files.add(forsendelse.getDokumentpakke().getHoveddokument());
        files.addAll(forsendelse.getDokumentpakke().getVedlegg());
        files.add(manifest);

        List<Reference> references = references(files);

        // Lag signatur-referanse for XaDES properties
        references.add(new Reference(new Transforms(asList(canonicalXmlTransform)), sha256DigestMethod, null, null, "#SignedProperties", xadesReferenceType));

        SignedInfo signedInfo = new SignedInfo(canonicalizationMethod, signatureMethod, references, null);

        X509Certificate certificate = avsender.getNoekkelpar().getSertifikat().getCertificate();
        // TODO: Er det riktig å bruke encoded versjon (ASN.1 DER) av sertifikatet?
        byte[] certificateDigestValue = sha1(avsender.getNoekkelpar().getSertifikat().getEncoded());

        DigestAlgAndValueType certificateDigest = new DigestAlgAndValueType(sha1DigestMethod, certificateDigestValue);
        X509IssuerSerialType certificateIssuer = new X509IssuerSerialType(certificate.getIssuerDN().getName(), certificate.getSerialNumber());
        SigningCertificate signingCertificate = new SigningCertificate(singletonList(new CertIDType(certificateDigest, certificateIssuer, null)));

        SignedSignatureProperties signedSignatureProperties = new SignedSignatureProperties().withSigningTime(DateTime.now()).withSigningCertificate(signingCertificate);
        SignedDataObjectProperties signedDataObjectProperties = new SignedDataObjectProperties().withDataObjectFormats(dataObjectFormats(files));
        SignedProperties signedProperties = new SignedProperties(signedSignatureProperties, signedDataObjectProperties, "SignedProperties");

        return new XAdESSignatures().withSignatures(new Signature()
                .withSignedInfo(signedInfo)
                .withObjects(new org.w3.xmldsig.Object().withContent(signedProperties)));
    }

    private List<DataObjectFormat> dataObjectFormats(List<AsicEAttachable> files) {
        List<DataObjectFormat> dataObjectFormats = new ArrayList<DataObjectFormat>();
        for (AsicEAttachable file : files) {
            dataObjectFormats.add(new DataObjectFormat().withMimeType(file.getMimeType()).withObjectReference(file.getFileName()));
        }
        return dataObjectFormats;
    }

    private List<Reference> references(List<AsicEAttachable> files) {
        List<Reference> references = new ArrayList<Reference>();
        for (AsicEAttachable file : files) {
            references.add(reference(file));
        }
        return references;
    }

    private Reference reference(AsicEAttachable file) {
        return new Reference(null, sha256DigestMethod, sha256(file.getBytes()), null, file.getFileName(), null);
    }
}
