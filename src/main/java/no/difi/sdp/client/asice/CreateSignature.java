package no.difi.sdp.client.asice;

import no.difi.sdp.client.domain.Avsender;
import no.difi.sdp.client.domain.Forsendelse;
import no.difi.sdp.client.domain.exceptions.KonfigurasjonException;
import no.difi.sdp.client.domain.exceptions.XmlKonfigurasjonException;
import org.etsi.uri._01903.v1_3.CertIDType;
import org.etsi.uri._01903.v1_3.DataObjectFormat;
import org.etsi.uri._01903.v1_3.DigestAlgAndValueType;
import org.etsi.uri._01903.v1_3.SignedDataObjectProperties;
import org.etsi.uri._01903.v1_3.SignedProperties;
import org.etsi.uri._01903.v1_3.SignedSignatureProperties;
import org.etsi.uri._01903.v1_3.SigningCertificate;
import org.etsi.uri._2918.v1_2.XAdESSignatures;
import org.joda.time.DateTime;
import org.w3.xmldsig.X509IssuerSerialType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.apache.commons.codec.digest.DigestUtils.sha1;
import static org.apache.commons.codec.digest.DigestUtils.sha256;

public class CreateSignature {

    private final String signedPropertiesType = "http://uri.etsi.org/01903#SignedProperties";
    private final org.w3.xmldsig.DigestMethod sha1DigestMethod = new org.w3.xmldsig.DigestMethod(emptyList(), "http://www.w3.org/2000/09/xmldsig#sha1");
    private final DigestMethod sha256DigestMethod;
    private final CanonicalizationMethod canonicalizationMethod;
    private final SignatureMethod signatureMethod;
    private final Transform canonicalXmlTransform;

    public CreateSignature() {
        XMLSignatureFactory xmlSignatureFactory = XMLSignatureFactory.getInstance("DOM");
        try {
            sha256DigestMethod = xmlSignatureFactory.newDigestMethod("http://www.w3.org/2001/04/xmlenc#sha256", null);
            canonicalizationMethod = xmlSignatureFactory.newCanonicalizationMethod("http://www.w3.org/2006/12/xml-c14n11", (C14NMethodParameterSpec) null);
            signatureMethod = xmlSignatureFactory.newSignatureMethod("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256", null);
            canonicalXmlTransform = xmlSignatureFactory.newTransform("http://www.w3.org/TR/2001/REC-xml-c14n-20010315", (TransformParameterSpec) null);
        } catch (NoSuchAlgorithmException e) {
            throw new KonfigurasjonException("Kunne ikke initialisere xml-signering", e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new KonfigurasjonException("Kunne ikke initialisere xml-signering", e);
        }
    }

    public Signature createSignature(Manifest manifest, Avsender avsender, Forsendelse forsendelse) {
        // List alle filer i Asic-E meldingen (hoveddokument, vedlegg og manifest)
        List<AsicEAttachable> files = new ArrayList<AsicEAttachable>();
        files.add(forsendelse.getDokumentpakke().getHoveddokument());
        files.addAll(forsendelse.getDokumentpakke().getVedlegg());
        files.add(manifest);

        XAdESSignatures xAdESSDocument = createXAdESSDocument(files, avsender);

        XMLSignatureFactory xmlSignatureFactory = XMLSignatureFactory.getInstance("DOM");

        // Lag signatur-referanse for alle filer
        List<Reference> references = references(xmlSignatureFactory, files);

        // Lag signatur-referanse for XaDES properties
        references.add(xmlSignatureFactory.newReference(
                "#SignedProperties",
                sha256DigestMethod,
                singletonList(canonicalXmlTransform),
                signedPropertiesType,
                null
        ));

        SignedInfo signedInfo = xmlSignatureFactory.newSignedInfo(
                canonicalizationMethod,
                signatureMethod,
                references
        );

        X509Certificate certificate = avsender.getNoekkelpar().getSertifikat().getCertificate();

        KeyInfoFactory keyInfoFactory = xmlSignatureFactory.getKeyInfoFactory();
        X509Data x509Data = keyInfoFactory.newX509Data(singletonList(certificate));
        KeyInfo keyInfo = keyInfoFactory.newKeyInfo(singletonList(x509Data));

        XMLSignature xmlSignature = xmlSignatureFactory.newXMLSignature(signedInfo, keyInfo);

        Jaxb.marshal(xAdESSDocument, new StreamResult(System.out));

        DOMResult domResult = new DOMResult();
        Jaxb.marshal(xAdESSDocument, domResult);
        Document document = (Document) domResult.getNode();
        Element element = document.getDocumentElement();

        // Explicitly mark the SignedProperties Id as an Document ID attribute, so that it will be eligble as a reference for signature.
        XPath xPath = XPathFactory.newInstance().newXPath();
        try {
            Element idElement = (Element) xPath.evaluate("//*[local-name()='SignedProperties']", document, XPathConstants.NODE);
            idElement.setIdAttribute("Id", true);

        } catch (XPathExpressionException e) {
            throw new XmlKonfigurasjonException("XPath på generert XML feilet.", e);
        }

        DOMSignContext domSignContext = new DOMSignContext(avsender.getNoekkelpar().getPrivateKey(), element);
        try {
            xmlSignature.sign(domSignContext);
        } catch (MarshalException e) {
            throw new XmlKonfigurasjonException("Klarte ikke å lese Asic-E XML for signering", e);
        } catch (XMLSignatureException e) {
            throw new XmlKonfigurasjonException("Klarte ikke å signere Asic-E element.", e);
        }

        return new Signature(document);
    }

    private XAdESSignatures createXAdESSDocument(List<AsicEAttachable> files, Avsender avsender) {
        X509Certificate certificate = avsender.getNoekkelpar().getSertifikat().getCertificate();
        // TODO: Er det riktig å bruke encoded versjon (ASN.1 DER) av sertifikatet?
        byte[] certificateDigestValue = sha1(avsender.getNoekkelpar().getSertifikat().getEncoded());

        DigestAlgAndValueType certificateDigest = new DigestAlgAndValueType(sha1DigestMethod, certificateDigestValue);
        X509IssuerSerialType certificateIssuer = new X509IssuerSerialType(certificate.getIssuerDN().getName(), certificate.getSerialNumber());
        SigningCertificate signingCertificate = new SigningCertificate(singletonList(new CertIDType(certificateDigest, certificateIssuer, null)));

        SignedSignatureProperties signedSignatureProperties = new SignedSignatureProperties().withSigningTime(DateTime.now()).withSigningCertificate(signingCertificate);
        SignedDataObjectProperties signedDataObjectProperties = new SignedDataObjectProperties().withDataObjectFormats(dataObjectFormats(files));
        SignedProperties signedProperties = new SignedProperties(signedSignatureProperties, signedDataObjectProperties, "SignedProperties");
        return new XAdESSignatures().withSignatures(new org.w3.xmldsig.Signature().withObjects(new org.w3.xmldsig.Object().withContent(signedProperties)));
    }

    private List<DataObjectFormat> dataObjectFormats(List<AsicEAttachable> files) {
        List<DataObjectFormat> dataObjectFormats = new ArrayList<DataObjectFormat>();
        for (AsicEAttachable file : files) {
            dataObjectFormats.add(new DataObjectFormat().withMimeType(file.getMimeType()).withObjectReference(file.getFileName()));
        }
        return dataObjectFormats;
    }

    private List<Reference> references(XMLSignatureFactory xmlSignatureFactory, List<AsicEAttachable> files) {
        List<Reference> references = new ArrayList<Reference>();
        for (AsicEAttachable file : files) {
            references.add(reference(xmlSignatureFactory, file));
        }
        return references;
    }

    private Reference reference(XMLSignatureFactory xmlSignatureFactory, AsicEAttachable file) {
        return xmlSignatureFactory.newReference(
                file.getFileName(),
                sha256DigestMethod,
                null,
                null,
                null,
                sha256(file.getBytes())
        );
    }
}
