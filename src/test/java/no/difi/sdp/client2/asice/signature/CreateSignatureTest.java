package no.difi.sdp.client2.asice.signature;

import static java.util.Arrays.asList;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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

import no.difi.sdp.client2.ObjectMother;
import no.difi.sdp.client2.asice.AsicEAttachable;
import no.difi.sdp.client2.domain.Noekkelpar;

import org.apache.commons.io.IOUtils;
import org.apache.jcp.xml.dsig.internal.dom.DOMSubTreeData;
import org.etsi.uri._01903.v1_3.DataObjectFormat;
import org.etsi.uri._01903.v1_3.DigestAlgAndValueType;
import org.etsi.uri._01903.v1_3.QualifyingProperties;
import org.etsi.uri._01903.v1_3.SignedDataObjectProperties;
import org.etsi.uri._01903.v1_3.SigningCertificate;
import org.etsi.uri._2918.v1_2.XAdESSignatures;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.w3.xmldsig.Reference;
import org.w3.xmldsig.SignedInfo;
import org.w3.xmldsig.X509IssuerSerialType;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

public class CreateSignatureTest {

    private CreateSignature sut;

    /**
     * SHA256 hash of "hoveddokument-innhold"
     */
    private final byte[] expectedHovedDokumentHash = new byte[] { 93, -36, 99, 92, -27, 39, 21, 31, 33, -127, 30, 77, 6, 49, 92, -48, -114, -61, -100, -126, -64, -70, 70, -38, 67, 93, -126, 62, -125, -7, -115, 123 };

    private Noekkelpar noekkelpar;
    private List<AsicEAttachable> files;

    private static final Jaxb2Marshaller marshaller;

    static {
        marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(XAdESSignatures.class, QualifyingProperties.class);
    }

    @Before
    public void setUp() throws Exception {
        noekkelpar = ObjectMother.noekkelpar();
        files = asList(
                file("hoveddokument.pdf", "hoveddokument-innhold".getBytes(), "application/pdf"),
                file("manifest.xml", "manifest-innhold".getBytes(), "application/xml")
        );

        sut = new CreateSignature();
    }

    @After
    public void tearDown() {
        DateTimeUtils.setCurrentMillisSystem();
    }

    @Test
    public void test_generated_signatures() {
        Signature signature = sut.createSignature(noekkelpar, files);
        XAdESSignatures xAdESSignatures = (XAdESSignatures) marshaller.unmarshal(new StreamSource(new ByteArrayInputStream(signature.getBytes())));

        assertThat(xAdESSignatures.getSignatures()).hasSize(1);
        org.w3.xmldsig.Signature dSignature = xAdESSignatures.getSignatures().get(0);
        verify_signed_info(dSignature.getSignedInfo());
        assertThat(dSignature.getSignatureValue()).isNotNull();
        assertThat(dSignature.getKeyInfo()).isNotNull();
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
    	for(Thread t : threads) {
    		t.join();
    	}
    	if (fails.get() > 0) {
    		fail("Signature validation failed");
    	}
    }

    @Test
    public void test_xades_signed_properties() {
        Signature signature = sut.createSignature(noekkelpar, files);

        XAdESSignatures xAdESSignatures = (XAdESSignatures) marshaller.unmarshal(new StreamSource(new ByteArrayInputStream(signature.getBytes())));
        org.w3.xmldsig.Object object = xAdESSignatures.getSignatures().get(0).getObjects().get(0);

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
        XAdESSignatures xAdESSignatures = (XAdESSignatures) marshaller.unmarshal(new StreamSource(new ByteArrayInputStream(signature.getBytes())));
        String uri = xAdESSignatures.getSignatures().get(0).getSignedInfo().getReferences().get(0).getURI();
        assertEquals("hoveddokument+%282%29.pdf", uri);
    }



    @Test
    public void test_pregenerated_xml() throws Exception {
        // Note: this is a very brittle test. it is meant to be guiding. If it fails, manually check if the changes to the XML makes sense. If they do, just update the expected XML.
        String expected = IOUtils.toString(this.getClass().getResourceAsStream("/asic/expected-asic-signature.xml"));

        // The signature partly depends on the exact time the original message was signed
        DateTime dateTime = new DateTime(2014, 5, 21, 17, 7, 15, 756, DateTimeZone.forOffsetHours(2));
        DateTimeUtils.setCurrentMillisFixed(dateTime.getMillis());

        Signature signature = sut.createSignature(noekkelpar, files);

        String actual = prettyPrint(signature);

        assertThat(actual).isEqualTo(expected);
    }

    private void verify_signed_data_object_properties(final SignedDataObjectProperties signedDataObjectProperties) {
        assertThat(signedDataObjectProperties.getDataObjectFormats()).hasSize(2); // One per file
        DataObjectFormat hoveddokumentDataObjectFormat = signedDataObjectProperties.getDataObjectFormats().get(0);
        assertThat(hoveddokumentDataObjectFormat.getObjectReference()).isEqualTo("#ID_0");
        assertThat(hoveddokumentDataObjectFormat.getMimeType()).isEqualTo("application/pdf");

        DataObjectFormat manifestDataObjectFormat = signedDataObjectProperties.getDataObjectFormats().get(1);
        assertThat(manifestDataObjectFormat.getObjectReference()).isEqualTo("#ID_1");
        assertThat(manifestDataObjectFormat.getMimeType()).isEqualTo("application/xml");
    }

    private void verify_signing_certificate(final SigningCertificate signingCertificate) {
        assertThat(signingCertificate.getCerts()).hasSize(1);

        DigestAlgAndValueType certDigest = signingCertificate.getCerts().get(0).getCertDigest();
        assertThat(certDigest.getDigestMethod().getAlgorithm()).isEqualTo("http://www.w3.org/2000/09/xmldsig#sha1");
        assertThat(certDigest.getDigestValue()).hasSize(20); // SHA1 is 160 bits => 20 bytes

        X509IssuerSerialType issuerSerial = signingCertificate.getCerts().get(0).getIssuerSerial();
        assertThat(issuerSerial.getX509IssuerName()).isEqualTo("CN=Avsender, OU=Avsender, O=Avsender, L=Oslo, ST=NO, C=NO");
        assertThat(issuerSerial.getX509SerialNumber()).isEqualTo(new BigInteger("589725471"));
    }

    private void verify_signed_info(final SignedInfo signedInfo) {
        assertThat(signedInfo.getCanonicalizationMethod().getAlgorithm()).isEqualTo("http://www.w3.org/TR/2001/REC-xml-c14n-20010315");
        assertThat(signedInfo.getSignatureMethod().getAlgorithm()).isEqualTo("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256");

        List<Reference> references = signedInfo.getReferences();
        assertThat(references).hasSize(3);
        assert_hovedokument_reference(references.get(0));
        assertThat(references.get(1).getURI()).isEqualTo("manifest.xml");
        verify_signed_properties_reference(references.get(2));
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
					for(AsicEAttachable file : files) {
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
	    	        Iterator i = signature.getSignedInfo().getReferences().iterator();
	    	        for (int j=0; i.hasNext(); j++) {
	    	            boolean refValid = ((javax.xml.crypto.dsig.Reference) i.next()).validate(valContext);
	    	            System.out.println("ref["+j+"] validity status: " + refValid);
	    	        }
	    	    }
	    	}
	    	return coreValidity;
    	} catch(Exception ex) {
    		ex.printStackTrace(System.err);
    		return false;
    	}
	}

	private void verify_signed_properties_reference(final Reference signedPropertiesReference) {
        assertThat(signedPropertiesReference.getURI()).isEqualTo("#SignedProperties");
        assertThat(signedPropertiesReference.getType()).isEqualTo("http://uri.etsi.org/01903#SignedProperties");
        assertThat(signedPropertiesReference.getDigestMethod().getAlgorithm()).isEqualTo("http://www.w3.org/2001/04/xmlenc#sha256");
        assertThat(signedPropertiesReference.getDigestValue()).hasSize(32); // SHA256 is 256 bits => 32 bytes
        assertThat(signedPropertiesReference.getTransforms().getTransforms().get(0).getAlgorithm()).isEqualTo("http://www.w3.org/TR/2001/REC-xml-c14n-20010315");
    }

    private void assert_hovedokument_reference(final Reference hovedDokumentReference) {
        assertThat(hovedDokumentReference.getURI()).isEqualTo("hoveddokument.pdf");
        assertThat(hovedDokumentReference.getDigestValue()).isEqualTo(expectedHovedDokumentHash);
        assertThat(hovedDokumentReference.getDigestMethod().getAlgorithm()).isEqualTo("http://www.w3.org/2001/04/xmlenc#sha256");
    }

    private AsicEAttachable file(final String fileName, final byte[] contents, final String mimeType) {
        return new AsicEAttachable() {
            @Override
            public String getFileName() { return fileName; }
            @Override
            public byte[] getBytes() { return contents; }
            @Override
            public String getMimeType() { return mimeType; }
        };
    }

    private String prettyPrint(final Signature signature) throws TransformerException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        StreamSource xmlSource = new StreamSource(new ByteArrayInputStream(signature.getBytes()));
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        DOMResult outputTarget = new DOMResult();
        transformer.transform(xmlSource, outputTarget);

        final DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
        final DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
        final LSSerializer writer = impl.createLSSerializer();

        writer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);
        writer.getDomConfig().setParameter("xml-declaration", Boolean.FALSE);

        return writer.writeToString(outputTarget.getNode());
    }

}
