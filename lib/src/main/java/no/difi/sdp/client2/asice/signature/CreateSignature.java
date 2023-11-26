/*
 * Copyright (C) Posten Norge AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package no.difi.sdp.client2.asice.signature;

import no.difi.sdp.client2.asice.AsicEAttachable;
import no.difi.sdp.client2.domain.Noekkelpar;
import no.difi.sdp.client2.domain.exceptions.KonfigurasjonException;
import no.difi.sdp.client2.domain.exceptions.XmlKonfigurasjonException;
import no.difi.sdp.client2.domain.exceptions.XmlValideringException;
import no.digipost.api.xml.SchemaResources;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.NodeSetData;
import javax.xml.crypto.URIDereferencer;
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
import javax.xml.validation.Schema;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.Certificate;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static no.difi.sdp.client2.domain.exceptions.SendException.AntattSkyldig.KLIENT;
import static org.apache.commons.codec.digest.DigestUtils.sha256;

@SuppressWarnings("FieldCanBeLocal")
public class CreateSignature {

    private static final String C14V1 = CanonicalizationMethod.INCLUSIVE;
    private static final String ASIC_NAMESPACE = "http://uri.etsi.org/2918/v1.2.1#";
    private static final String SIGNED_PROPERTIES_TYPE = "http://uri.etsi.org/01903#SignedProperties";

    private final DigestMethod sha256DigestMethod;
    private final CanonicalizationMethod canonicalizationMethod;
    private final Transform canonicalXmlTransform;

    private final DomUtils domUtils;
    private final CreateXAdESArtifacts createXAdESProperties;
    private final Schema schema;

    public CreateSignature() {
        this(Clock.systemDefaultZone());
    }

    public CreateSignature(Clock clock) {
        this(new CreateXAdESArtifacts(clock));
    }

    public CreateSignature(CreateXAdESArtifacts createXAdESProperties) {
        this.domUtils = new DomUtils();
        this.createXAdESProperties = createXAdESProperties;
        try {
            XMLSignatureFactory xmlSignatureFactory = getSignatureFactory();
            this.sha256DigestMethod = xmlSignatureFactory.newDigestMethod(DigestMethod.SHA256, null);
            this.canonicalizationMethod = xmlSignatureFactory.newCanonicalizationMethod(C14V1, (C14NMethodParameterSpec) null);
            this.canonicalXmlTransform = xmlSignatureFactory.newTransform(C14V1, (TransformParameterSpec) null);
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            throw new KonfigurasjonException("Kunne ikke initialisere xml-signering, fordi " + e.getClass().getSimpleName() + ": '" + e.getMessage() + "'", e);
        }

        this.schema = SchemaResources.createSchema(SchemaResources.ASICE_SCHEMA);
    }

    public Signature createSignature(final Noekkelpar noekkelpar, final List<AsicEAttachable> attachedFiles) throws XmlValideringException {
        XMLSignatureFactory xmlSignatureFactory = getSignatureFactory();
        SignatureMethod signatureMethod = getSignatureMethod(xmlSignatureFactory);

        // Generer XAdES-dokument som skal signeres, informasjon om nøkkel brukt til signering og informasjon om hva som er signert
        XAdESArtifacts xadesArtifacts = createXAdESProperties.createArtifactsToSign(attachedFiles, noekkelpar.getVirksomhetssertifikat());

        // Lag signatur-referanse for alle filer
        List<Reference> references = references(xmlSignatureFactory, attachedFiles);

        // Lag signatur-referanse for XaDES properties
        references.add(xmlSignatureFactory.newReference(
                xadesArtifacts.signablePropertiesReferenceUri,
                sha256DigestMethod,
                singletonList(canonicalXmlTransform),
                SIGNED_PROPERTIES_TYPE,
                null
        ));


        KeyInfo keyInfo = keyInfo(xmlSignatureFactory, noekkelpar.getVirksomhetssertifikatKjede());
        SignedInfo signedInfo = xmlSignatureFactory.newSignedInfo(canonicalizationMethod, signatureMethod, references);

        // Definer signatur over XAdES-dokument
        XMLObject xmlObject = xmlSignatureFactory.newXMLObject(singletonList(new DOMStructure(xadesArtifacts.document.getDocumentElement())), null, null, null);
        XMLSignature xmlSignature = xmlSignatureFactory.newXMLSignature(signedInfo, keyInfo, singletonList(xmlObject), "Signature", null);

        Document signedDocument = domUtils.newEmptyXmlDocument();
        DOMSignContext signContext = new DOMSignContext(noekkelpar.getVirksomhetssertifikatPrivatnoekkel(), addXAdESSignaturesElement(signedDocument));
        signContext.setURIDereferencer(signedPropertiesURIDereferencer(xadesArtifacts, xmlSignatureFactory));

        try {
            xmlSignature.sign(signContext);
        } catch (MarshalException e) {
            throw new XmlKonfigurasjonException("Klarte ikke å lese ASiC-E XML for signering", e);
        } catch (XMLSignatureException e) {
            throw new XmlKonfigurasjonException("Klarte ikke å signere ASiC-E element.", e);
        }

        try {
            schema.newValidator().validate(new DOMSource(signedDocument));
        } catch (SAXException | IOException e) {
            throw new XmlValideringException(
                    "Failed to validate generated signature.xml because " + e.getClass().getSimpleName() + ": '" + e.getMessage() + "'. " +
                    "Verify that the input is valid and that there are no illegal symbols in file names etc.", KLIENT, e);
        }
        return new Signature(domUtils.serializeToXml(signedDocument));
    }

    private URIDereferencer signedPropertiesURIDereferencer(XAdESArtifacts xadesArtifacts, XMLSignatureFactory signatureFactory) {
        return (uriReference, context) -> {
            if (xadesArtifacts.signablePropertiesReferenceUri.equals(uriReference.getURI())) {
                return (NodeSetData<Node>) domUtils.allNodesBelow(xadesArtifacts.signableProperties)::iterator;
            }
            return signatureFactory.getURIDereferencer().dereference(uriReference, context);
        };
    }
     private static Element addXAdESSignaturesElement(Document doc) {
        return (Element) doc.appendChild(doc.createElementNS(ASIC_NAMESPACE, "XAdESSignatures"));
    }

	private static SignatureMethod getSignatureMethod(final XMLSignatureFactory xmlSignatureFactory) {
        try {
            return xmlSignatureFactory.newSignatureMethod("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256", null);
        } catch (NoSuchAlgorithmException e) {
            throw new KonfigurasjonException("Kunne ikke initialisere xml-signering", e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new KonfigurasjonException("Kunne ikke initialisere xml-signering", e);
        }
	}

    private List<Reference> references(final XMLSignatureFactory xmlSignatureFactory, final List<AsicEAttachable> files) {
        List<Reference> result = new ArrayList<Reference>();
        for (int i = 0; i < files.size(); i++) {
            try {
	            String signatureElementId = "ID_" + i;
                String uri = URLEncoder.encode(files.get(i).getFileName(), "UTF-8");
                Reference reference = xmlSignatureFactory.newReference(uri, sha256DigestMethod, null, null, signatureElementId, sha256(files.get(i).getBytes()));
                result.add(reference);
            } catch(UnsupportedEncodingException e) {
            	throw new RuntimeException(e);
            }

        }
        return result;
    }

    private static KeyInfo keyInfo(final XMLSignatureFactory xmlSignatureFactory, final Certificate[] sertifikater) {
        KeyInfoFactory keyInfoFactory = xmlSignatureFactory.getKeyInfoFactory();
        X509Data x509Data = keyInfoFactory.newX509Data(asList(sertifikater));
        return keyInfoFactory.newKeyInfo(singletonList(x509Data));
    }

    private static XMLSignatureFactory getSignatureFactory() {
        try {
            return XMLSignatureFactory.getInstance("DOM", "XMLDSig");
        } catch (NoSuchProviderException e) {
            throw new KonfigurasjonException("Fant ikke XML Digital Signature-provider. Biblioteket avhenger av default Java-provider.");
        }
    }

}
