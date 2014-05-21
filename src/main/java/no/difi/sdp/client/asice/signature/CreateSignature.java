package no.difi.sdp.client.asice.signature;

import no.difi.sdp.client.asice.AsicEAttachable;
import no.difi.sdp.client.asice.Jaxb;
import no.difi.sdp.client.asice.Manifest;
import no.difi.sdp.client.asice.Signature;
import no.difi.sdp.client.domain.Avsender;
import no.difi.sdp.client.domain.Forsendelse;
import no.difi.sdp.client.domain.Sertifikat;
import no.difi.sdp.client.domain.exceptions.KonfigurasjonException;
import no.difi.sdp.client.domain.exceptions.XmlKonfigurasjonException;
import org.etsi.uri._2918.v1_2.XAdESSignatures;
import org.w3c.dom.Document;

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

    private final String signedPropertiesType = "http://uri.etsi.org/01903#SignedProperties";
    private final DigestMethod sha256DigestMethod;
    private final CanonicalizationMethod canonicalizationMethod;
    private final SignatureMethod signatureMethod;
    private final Transform canonicalXmlTransform;

    private final CreateXAdESProperties createXAdESProperties;

    public CreateSignature() {
        createXAdESProperties = new CreateXAdESProperties();
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

    public Signature createSignature(Manifest manifest, Avsender avsender, Forsendelse forsendelse) {
        XMLSignatureFactory xmlSignatureFactory = XMLSignatureFactory.getInstance("DOM");

        // List alle filer i Asic-E meldingen (hoveddokument, vedlegg og manifest)
        List<AsicEAttachable> files = filesToInclude(manifest, forsendelse);

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

        Sertifikat sertifikat = avsender.getNoekkelpar().getSertifikat();

        // Generer XAdES-dokument som skal signeres, informasjon om nøkkel brukt til signering og informasjon om hva som er signert
        Document documentToSign = createXAdESProperties.createPropertiesToSign(files, sertifikat);
        KeyInfo keyInfo = keyInfo(xmlSignatureFactory, sertifikat);
        SignedInfo signedInfo = xmlSignatureFactory.newSignedInfo(canonicalizationMethod, signatureMethod, references);

        // Definer signatur over XAdES-dokument
        XMLObject xmlObject = xmlSignatureFactory.newXMLObject(Collections.singletonList(new DOMStructure(documentToSign.getDocumentElement())), null, null, null);
        XMLSignature xmlSignature = xmlSignatureFactory.newXMLSignature(signedInfo, keyInfo, Collections.singletonList(xmlObject), null, null);

        DOMSignContext domSignContext = new DOMSignContext(avsender.getNoekkelpar().getPrivateKey(), documentToSign);
        try {
            xmlSignature.sign(domSignContext);
        } catch (MarshalException e) {
            throw new XmlKonfigurasjonException("Klarte ikke å lese Asic-E XML for signering", e);
        } catch (XMLSignatureException e) {
            throw new XmlKonfigurasjonException("Klarte ikke å signere Asic-E element.", e);
        }

        // Pakk Signatur inn i XAdES-konvolutt
        org.w3.xmldsig.Signature signature = Jaxb.unmarshal(new DOMSource(documentToSign), org.w3.xmldsig.Signature.class);
        XAdESSignatures xAdESSignatures = new XAdESSignatures(Collections.singletonList(signature));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Jaxb.marshal(xAdESSignatures, new StreamResult(outputStream));

        return new Signature(outputStream.toByteArray());
    }

    private List<AsicEAttachable> filesToInclude(Manifest manifest, Forsendelse forsendelse) {
        List<AsicEAttachable> files = new ArrayList<AsicEAttachable>();
        files.add(forsendelse.getDokumentpakke().getHoveddokument());
        files.addAll(forsendelse.getDokumentpakke().getVedlegg());
        files.add(manifest);
        return files;
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
        X509Data x509Data = keyInfoFactory.newX509Data(singletonList(sertifikat.getCertificate()));
        return keyInfoFactory.newKeyInfo(singletonList(x509Data));
    }

}
