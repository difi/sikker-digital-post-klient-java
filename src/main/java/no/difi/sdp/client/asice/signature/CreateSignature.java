package no.difi.sdp.client.asice.signature;

import no.difi.sdp.client.asice.AsicEAttachable;
import no.difi.sdp.client.domain.Noekkelpar;
import no.difi.sdp.client.domain.Sertifikat;
import no.difi.sdp.client.domain.exceptions.KonfigurasjonException;
import no.difi.sdp.client.domain.exceptions.XmlKonfigurasjonException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLObject;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.apache.commons.codec.digest.DigestUtils.sha256;

public class CreateSignature {

    private final String asicNamespace = "http://uri.etsi.org/2918/v1.2.1#";
    private final String signedPropertiesType = "http://uri.etsi.org/01903#SignedProperties";
    private final DigestMethod sha256DigestMethod;
    private final CanonicalizationMethod canonicalizationMethod;
    private final SignatureMethod signatureMethod;
    private final Transform canonicalXmlTransform;

    private final CreateXAdESProperties createXAdESProperties;
    private final TransformerFactory transformerFactory;

    public CreateSignature() {
        createXAdESProperties = new CreateXAdESProperties();
        transformerFactory = TransformerFactory.newInstance();
        try {
            XMLSignatureFactory xmlSignatureFactory = XMLSignatureFactory.getInstance("DOM");
            sha256DigestMethod = xmlSignatureFactory.newDigestMethod(DigestMethod.SHA256, null);
            canonicalizationMethod = xmlSignatureFactory.newCanonicalizationMethod("http://www.w3.org/2006/12/xml-c14n11", (C14NMethodParameterSpec) null);
            signatureMethod = xmlSignatureFactory.newSignatureMethod("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256", null);
            canonicalXmlTransform = xmlSignatureFactory.newTransform("http://www.w3.org/TR/2001/REC-xml-c14n-20010315", (TransformParameterSpec) null);
        } catch (NoSuchAlgorithmException e) {
            throw new KonfigurasjonException("Kunne ikke initialisere xml-signering", e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new KonfigurasjonException("Kunne ikke initialisere xml-signering", e);
        }
    }

    public Signature createSignature(Noekkelpar noekkelpar, List<AsicEAttachable> attachedFiles) {
        XMLSignatureFactory xmlSignatureFactory = XMLSignatureFactory.getInstance("DOM");

        // Lag signatur-referanse for alle filer
        List<Reference> references = references(xmlSignatureFactory, attachedFiles);

        // Lag signatur-referanse for XaDES properties
        references.add(xmlSignatureFactory.newReference(
                "#SignedProperties",
                sha256DigestMethod,
                singletonList(canonicalXmlTransform),
                signedPropertiesType,
                null
        ));

        // Generer XAdES-dokument som skal signeres, informasjon om nøkkel brukt til signering og informasjon om hva som er signert
        Document document = createXAdESProperties.createPropertiesToSign(attachedFiles, noekkelpar.getSertifikat());

        KeyInfo keyInfo = keyInfo(xmlSignatureFactory, noekkelpar.getSertifikat());
        SignedInfo signedInfo = xmlSignatureFactory.newSignedInfo(canonicalizationMethod, signatureMethod, references);

        // Definer signatur over XAdES-dokument
        XMLObject xmlObject = xmlSignatureFactory.newXMLObject(Collections.singletonList(new DOMStructure(document.getDocumentElement())), null, null, null);
        XMLSignature xmlSignature = xmlSignatureFactory.newXMLSignature(signedInfo, keyInfo, Collections.singletonList(xmlObject), null, null);

        try {
            xmlSignature.sign(new DOMSignContext(noekkelpar.getPrivateKey(), document));
        } catch (MarshalException e) {
            throw new XmlKonfigurasjonException("Klarte ikke å lese ASiC-E XML for signering", e);
        } catch (XMLSignatureException e) {
            throw new XmlKonfigurasjonException("Klarte ikke å signere ASiC-E element.", e);
        }

        // Pakk Signatur inn i XAdES-konvolutt
        wrapSignatureInXADeSEnvelope(document);

        ByteArrayOutputStream outputStream;
        try {
            outputStream = new ByteArrayOutputStream();
            transformerFactory.newTransformer().transform(new DOMSource(document), new StreamResult(outputStream));
        } catch (TransformerException e) {
            throw new KonfigurasjonException("Klarte ikke å serialisere XML", e);
        }

        return new Signature(outputStream.toByteArray());
    }

    private List<Reference> references(XMLSignatureFactory xmlSignatureFactory, List<AsicEAttachable> files) {
        List<Reference> references = new ArrayList<Reference>();
        for (AsicEAttachable file : files) {
            Reference reference = xmlSignatureFactory.newReference(file.getFileName(), sha256DigestMethod, null, null, null, sha256(file.getBytes()));
            references.add(reference);
        }
        return references;
    }

    private KeyInfo keyInfo(XMLSignatureFactory xmlSignatureFactory, Sertifikat sertifikat) {
        KeyInfoFactory keyInfoFactory = xmlSignatureFactory.getKeyInfoFactory();
        X509Data x509Data = keyInfoFactory.newX509Data(singletonList(sertifikat.getX509Certificate()));
        return keyInfoFactory.newKeyInfo(singletonList(x509Data));
    }

    private void wrapSignatureInXADeSEnvelope(Document document) {
        Node signatureElement = document.removeChild(document.getDocumentElement());
        Element xadesElement = document.createElementNS(asicNamespace, "XAdESSignatures");
        xadesElement.appendChild(signatureElement);
        document.appendChild(xadesElement);
    }

}
