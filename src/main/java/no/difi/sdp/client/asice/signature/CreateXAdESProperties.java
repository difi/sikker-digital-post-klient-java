package no.difi.sdp.client.asice.signature;

import no.difi.sdp.client.asice.AsicEAttachable;
import no.difi.sdp.client.asice.Jaxb;
import no.difi.sdp.client.domain.Sertifikat;
import no.difi.sdp.client.domain.exceptions.XmlKonfigurasjonException;
import org.etsi.uri._01903.v1_3.CertIDType;
import org.etsi.uri._01903.v1_3.DataObjectFormat;
import org.etsi.uri._01903.v1_3.DigestAlgAndValueType;
import org.etsi.uri._01903.v1_3.QualifyingProperties;
import org.etsi.uri._01903.v1_3.SignedDataObjectProperties;
import org.etsi.uri._01903.v1_3.SignedProperties;
import org.etsi.uri._01903.v1_3.SignedSignatureProperties;
import org.etsi.uri._01903.v1_3.SigningCertificate;
import org.joda.time.DateTime;
import org.w3.xmldsig.X509IssuerSerialType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.transform.dom.DOMResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.apache.commons.codec.digest.DigestUtils.sha1;

class CreateXAdESProperties {

    private final org.w3.xmldsig.DigestMethod sha1DigestMethod = new org.w3.xmldsig.DigestMethod(emptyList(), DigestMethod.SHA1);

    public Document createPropertiesToSign(List<AsicEAttachable> files, Sertifikat sertifikat) {
        X509Certificate certificate = sertifikat.getCertificate();
        // TODO: Er det riktig å bruke encoded versjon (ASN.1 DER) av sertifikatet?
        byte[] certificateDigestValue = sha1(sertifikat.getEncoded());

        DigestAlgAndValueType certificateDigest = new DigestAlgAndValueType(sha1DigestMethod, certificateDigestValue);
        X509IssuerSerialType certificateIssuer = new X509IssuerSerialType(certificate.getIssuerDN().getName(), certificate.getSerialNumber());
        SigningCertificate signingCertificate = new SigningCertificate(singletonList(new CertIDType(certificateDigest, certificateIssuer, null)));

        SignedSignatureProperties signedSignatureProperties = new SignedSignatureProperties().withSigningTime(DateTime.now()).withSigningCertificate(signingCertificate);
        SignedDataObjectProperties signedDataObjectProperties = new SignedDataObjectProperties().withDataObjectFormats(dataObjectFormats(files));
        SignedProperties signedProperties = new SignedProperties(signedSignatureProperties, signedDataObjectProperties, "SignedProperties");
        QualifyingProperties qualifyingProperties = new QualifyingProperties().withSignedProperties(signedProperties);

        DOMResult domResult = new DOMResult();
        Jaxb.marshal(qualifyingProperties, domResult);
        Document document = (Document) domResult.getNode();

        // Explicitly mark the SignedProperties Id as an Document ID attribute, so that it will be eligble as a reference for signature.
        // If not, it will not be treated a something to sign.
        markAsIdProperty(document, "SignedProperties", "Id");

        return document;
    }

    private List<DataObjectFormat> dataObjectFormats(List<AsicEAttachable> files) {
        List<DataObjectFormat> dataObjectFormats = new ArrayList<DataObjectFormat>();
        for (AsicEAttachable file : files) {
            dataObjectFormats.add(new DataObjectFormat().withMimeType(file.getMimeType()).withObjectReference(file.getFileName()));
        }
        return dataObjectFormats;
    }

    private void markAsIdProperty(Document document, final String elementName, String property) {
        XPath xPath = XPathFactory.newInstance().newXPath();
        try {
            Element idElement = (Element) xPath.evaluate("//*[local-name()='" + elementName + "']", document, XPathConstants.NODE);
            idElement.setIdAttribute(property, true);

        } catch (XPathExpressionException e) {
            throw new XmlKonfigurasjonException("XPath på generert XML feilet.", e);
        }
    }
}
