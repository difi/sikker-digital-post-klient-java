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

import no.difi.sdp.client2.ObjectMother;
import no.difi.sdp.client2.asice.AsicEAttachable;
import no.difi.sdp.client2.domain.Noekkelpar;
import no.digipost.api.xml.JaxbMarshaller;
import no.digipost.org.w3.xmldsig.Reference;
import no.digipost.org.w3.xmldsig.SignedInfo;
import no.digipost.org.w3.xmldsig.X509IssuerSerialType;
import no.digipost.time.ControllableClock;
import org.apache.commons.io.IOUtils;
import org.apache.jcp.xml.dsig.internal.dom.DOMSubTreeData;
import org.etsi.uri._01903.v1_3.DataObjectFormat;
import org.etsi.uri._01903.v1_3.DigestAlgAndValueType;
import org.etsi.uri._01903.v1_3.QualifyingProperties;
import org.etsi.uri._01903.v1_3.SignedDataObjectProperties;
import org.etsi.uri._01903.v1_3.SigningCertificate;
import org.etsi.uri._2918.v1_2.XAdESSignatures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import javax.xml.crypto.Data;
import javax.xml.crypto.OctetStreamData;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.URIReference;
import javax.xml.crypto.URIReferenceException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Arrays.asList;
import static no.difi.sdp.client2.internal.SdpTimeConstants.UTC;
import static no.digipost.DiggBase.nonNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class CreateSignatureTest {

    private static final JaxbMarshaller marshaller = JaxbMarshaller.marshallerForClasses(asList(XAdESSignatures.class, QualifyingProperties.class));

    private final ControllableClock clock = ControllableClock.freezedAt(Instant.now(), UTC);

    /**
     * SHA256 hash of "hoveddokument-innhold"
     */
    private final byte[] expectedHovedDokumentHash = new byte[]{93, -36, 99, 92, -27, 39, 21, 31, 33, -127, 30, 77, 6, 49, 92, -48, -114, -61, -100, -126, -64, -70, 70, -38, 67, 93, -126, 62, -125, -7, -115, 123};
    private CreateSignature sut;
    private Noekkelpar noekkelpar;
    private List<AsicEAttachable> files;

    @BeforeEach
    public void setUp() throws Exception {
        noekkelpar = ObjectMother.selvsignertNoekkelparUtenTrustStore();
        files = asList(
            file("hoveddokument.pdf", "hoveddokument-innhold".getBytes(), "application/pdf"),
            file("lenke.xml", "hoveddokumentdata".getBytes(), "application/vnd.difi.dpi.lenke+xml"),
            file("manifest.xml", "manifest-innhold".getBytes(), "application/xml")
        );

        sut = new CreateSignature(clock);
    }


    @Test
    public void test_generated_signatures() {
        Signature signature = sut.createSignature(noekkelpar, files);
        XAdESSignatures xAdESSignatures = marshaller.unmarshal(signature.getBytes(), XAdESSignatures.class);

        assertThat(xAdESSignatures.getSignatures(), hasSize(1));
        no.digipost.org.w3.xmldsig.Signature dSignature = xAdESSignatures.getSignatures().get(0);
        verify_signed_info(dSignature.getSignedInfo());
        assertThat(dSignature.getSignatureValue(), notNullValue());
        assertThat(dSignature.getKeyInfo(), notNullValue());
    }

    @Test
    public void multithreaded_signing() throws Exception {
        List<Thread> threads = new ArrayList<Thread>();
        final AtomicInteger fails = new AtomicInteger(0);
        for (int i = 0; i < 50; i++) {
            Thread t = new Thread() {
                @Override
                public void run() {
                    for (int j = 0; j < 20; j++) {
                        Signature signature = sut.createSignature(noekkelpar, files);
                        if (!verify_signature(signature)) {
                            fails.incrementAndGet();
                        }
                        if (fails.get() > 0) {
                            break;
                        }
                    }
                }
            };
            threads.add(t);
            t.start();
        }
        for (Thread t : threads) {
            t.join();
        }
        if (fails.get() > 0) {
            fail("Signature validation failed");
        }
    }

    @Test
    public void test_xades_signed_properties() {
        Signature signature = sut.createSignature(noekkelpar, files);

        XAdESSignatures xAdESSignatures = marshaller.unmarshal(signature.getBytes(), XAdESSignatures.class);
        no.digipost.org.w3.xmldsig.Object object = xAdESSignatures.getSignatures().get(0).getObjects().get(0);

        QualifyingProperties xadesProperties = (QualifyingProperties) object.getContent().get(0);
        SigningCertificate signingCertificate = xadesProperties.getSignedProperties().getSignedSignatureProperties().getSigningCertificate();
        verify_signing_certificate(signingCertificate);

        SignedDataObjectProperties signedDataObjectProperties = xadesProperties.getSignedProperties().getSignedDataObjectProperties();
        verify_signed_data_object_properties(signedDataObjectProperties);
    }

    @Test
    public void should_support_filenames_with_spaces_and_other_characters() {
        List<AsicEAttachable> otherFiles = asList(
            file("hoveddokument (2).pdf", "hoveddokument-innhold".getBytes(), "application/pdf"),
            file("manifest.xml", "manifest-innhold".getBytes(), "application/xml")
        );

        Signature signature = sut.createSignature(noekkelpar, otherFiles);
        XAdESSignatures xAdESSignatures = marshaller.unmarshal(signature.getBytes(), XAdESSignatures.class);
        String uri = xAdESSignatures.getSignatures().get(0).getSignedInfo().getReferences().get(0).getURI();
        assertEquals("hoveddokument+%282%29.pdf", uri);
    }


    @Test
    public void test_pregenerated_xml() throws Exception {
        // Note: this is a very brittle test. it is meant to be guiding. If it fails, manually check if the changes to the XML makes sense. If they do, just update the expected XML.

        byte[] expected;
        try (InputStream expectedStream = nonNull("/asic/expected-asic-signature.xml", getClass()::getResourceAsStream)) {
            expected = IOUtils.toByteArray(expectedStream);
        }

        // The signature partly depends on the exact time the original message was signed
        clock.set(ZonedDateTime.of(2014, 5, 21, 15, 7, 15, 756_000_000, UTC));

        Signature signature = sut.createSignature(noekkelpar, files);

        String actual = prettyPrint(signature);

        assertEquals(prettyPrint(expected), actual);
    }

    private void verify_signed_data_object_properties(final SignedDataObjectProperties signedDataObjectProperties) {
        assertThat(signedDataObjectProperties.getDataObjectFormats(), hasSize(3)); // One per file
        DataObjectFormat hoveddokumentDataObjectFormat = signedDataObjectProperties.getDataObjectFormats().get(0);
        assertThat(hoveddokumentDataObjectFormat.getObjectReference(), equalTo("#ID_0"));
        assertThat(hoveddokumentDataObjectFormat.getMimeType(), equalTo("application/pdf"));

        DataObjectFormat metadataDataObjectFormat = signedDataObjectProperties.getDataObjectFormats().get(1);
        assertThat(metadataDataObjectFormat.getObjectReference(), equalTo("#ID_1"));
        assertThat(metadataDataObjectFormat.getMimeType(), equalTo("application/vnd.difi.dpi.lenke+xml"));

        DataObjectFormat manifestDataObjectFormat = signedDataObjectProperties.getDataObjectFormats().get(2);
        assertThat(manifestDataObjectFormat.getObjectReference(), equalTo("#ID_2"));
        assertThat(manifestDataObjectFormat.getMimeType(), equalTo("application/xml"));
    }

    private void verify_signing_certificate(final SigningCertificate signingCertificate) {
        assertThat(signingCertificate.getCerts(), hasSize(1));

        DigestAlgAndValueType certDigest = signingCertificate.getCerts().get(0).getCertDigest();
        assertThat(certDigest.getDigestMethod().getAlgorithm(), equalTo("http://www.w3.org/2000/09/xmldsig#sha1"));
        assertThat(certDigest.getDigestValue().length, is(20)); // SHA1 is 160 bits => 20 bytes

        X509IssuerSerialType issuerSerial = signingCertificate.getCerts().get(0).getIssuerSerial();
        assertThat(issuerSerial.getX509IssuerName(), equalTo("CN=Avsender, OU=Avsender, O=Avsender, L=Oslo, ST=NO, C=NO"));
        assertThat(issuerSerial.getX509SerialNumber(), equalTo(new BigInteger("589725471")));
    }

    private void verify_signed_info(final SignedInfo signedInfo) {
        assertThat(signedInfo.getCanonicalizationMethod().getAlgorithm(), equalTo("http://www.w3.org/TR/2001/REC-xml-c14n-20010315"));
        assertThat(signedInfo.getSignatureMethod().getAlgorithm(), equalTo("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256"));

        List<Reference> references = signedInfo.getReferences();
        assertThat(references, hasSize(4));
        assert_hovedokument_reference(references.get(0));
        assertThat(references.get(1).getURI(), equalTo("lenke.xml"));
        assertThat(references.get(2).getURI(), equalTo("manifest.xml"));
        verify_signed_properties_reference(references.get(3));
    }

    private boolean verify_signature(final Signature signature2) {
        try {
            signature2.getBytes();
            DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
            fac.setNamespaceAware(true);
            DocumentBuilder builder = fac.newDocumentBuilder();
            final Document doc = builder.parse(new ByteArrayInputStream(signature2.getBytes()));
            //System.err.println(new String(signature2.getBytes()));
            NodeList nl = doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
            DOMValidateContext valContext = new DOMValidateContext
                (noekkelpar.getVirksomhetssertifikat().getX509Certificate().getPublicKey(), nl.item(0));
            valContext.setURIDereferencer(new URIDereferencer() {
                @Override
                public Data dereference(final URIReference uriReference, final XMLCryptoContext context) throws URIReferenceException {
                    //System.out.println("$$$$ " + uriReference.getURI());
                    for (AsicEAttachable file : files) {
                        if (file.getFileName().equals(uriReference.getURI().toString())) {
                            return new OctetStreamData(new ByteArrayInputStream(file.getBytes()));
                        }
                    }
                    uriReference.getURI().toString().replace("#", "");
                    Node element = doc.getElementsByTagName("SignedProperties").item(0);
                    return new DOMSubTreeData(element, false);

                }
            });
            XMLSignatureFactory fact = XMLSignatureFactory.getInstance("DOM");
            XMLSignature signature = fact.unmarshalXMLSignature(valContext);
            boolean coreValidity = signature.validate(valContext);
            if (coreValidity == false) {
                System.err.println("Signature failed core validation");
                boolean sv = signature.getSignatureValue().validate(valContext);
                System.out.println("signature validation status: " + sv);
                if (sv == false) {
                    // Check the validation status of each Reference.
                    Iterator<javax.xml.crypto.dsig.Reference> i = signature.getSignedInfo().getReferences().iterator();
                    for (int j = 0; i.hasNext(); j++) {
                        boolean refValid = i.next().validate(valContext);
                        System.out.println("ref[" + j + "] validity status: " + refValid);
                    }
                }
            }
            return coreValidity;
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            return false;
        }
    }

    private void verify_signed_properties_reference(final Reference signedPropertiesReference) {
        assertThat(signedPropertiesReference.getURI(), equalTo("#SignedProperties"));
        assertThat(signedPropertiesReference.getType(), equalTo("http://uri.etsi.org/01903#SignedProperties"));
        assertThat(signedPropertiesReference.getDigestMethod().getAlgorithm(), equalTo("http://www.w3.org/2001/04/xmlenc#sha256"));
        assertThat(signedPropertiesReference.getDigestValue().length, is(32)); // SHA256 is 256 bits => 32 bytes
        assertThat(signedPropertiesReference.getTransforms().getTransforms().get(0).getAlgorithm(), equalTo("http://www.w3.org/TR/2001/REC-xml-c14n-20010315"));
    }

    private void assert_hovedokument_reference(final Reference hovedDokumentReference) {
        assertThat(hovedDokumentReference.getURI(), equalTo("hoveddokument.pdf"));
        assertThat(hovedDokumentReference.getDigestValue(), equalTo(expectedHovedDokumentHash));
        assertThat(hovedDokumentReference.getDigestMethod().getAlgorithm(), equalTo("http://www.w3.org/2001/04/xmlenc#sha256"));
    }

    private AsicEAttachable file(final String fileName, final byte[] contents, final String mimeType) {
        return new AsicEAttachable() {
            @Override
            public String getFileName() {
                return fileName;
            }

            @Override
            public byte[] getBytes() {
                return contents;
            }

            @Override
            public String getMimeType() {
                return mimeType;
            }
        };
    }

    private String prettyPrint(final Signature signature) throws TransformerException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        return prettyPrint(signature.getBytes());
    }

    private String prettyPrint(final byte[] xml) throws TransformerException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        StreamSource xmlSource = new StreamSource(new ByteArrayInputStream(xml));
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        DOMResult outputTarget = new DOMResult();
        transformer.transform(xmlSource, outputTarget);

        final DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
        final DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
        final LSSerializer writer = impl.createLSSerializer();

        writer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);
        writer.getDomConfig().setParameter("xml-declaration", Boolean.FALSE);

        return writer.writeToString(outputTarget.getNode())
            /**
             * The Base64 signatures produced on Java 11 includes '\r' end-of-line characters encoded as &#13; or &#xd;,
             * because of using the java.util.Base64 Mime Encoder internally. This is by specification, and
             * handled when validating the signature, though it creates a diff when comparing the XML to
             * what is expected, so we simply strip it away here for the tests to run on both JDK 8 and 11.
             *
             * https://issues.apache.org/jira/browse/SANTUARIO-494
             * https://issues.apache.org/jira/browse/SANTUARIO-482
             */
            .replaceAll("&#13;", "")
            .replaceAll("&#xd;", "");
    }

}
