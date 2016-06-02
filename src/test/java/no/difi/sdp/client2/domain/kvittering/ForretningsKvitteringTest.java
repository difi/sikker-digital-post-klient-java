package no.difi.sdp.client2.domain.kvittering;

import no.digipost.api.representations.EbmsAktoer;
import no.digipost.api.representations.EbmsApplikasjonsKvittering;
import no.digipost.api.xml.Marshalling;
import org.junit.Test;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;

import static org.fest.assertions.api.Assertions.assertThat;

import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.io.StringReader;

public class ForretningsKvitteringTest {

    @Test
    public void testKonstruktør_initializesProperly() {
        EbmsBekreftbar ebmsBekreftbar = new EbmsBekreftbar() {
            @Override
            public String getMeldingsId() {
                return "MeldingsId";
            }

            @Override
            public String getReferanser() {
                return "Referanser";
            }
        };
        Kvitteringsinfo kvitteringsinfo = new Kvitteringsinfo();
        LeveringsKvittering leveringsKvittering = new LeveringsKvittering(ebmsBekreftbar, kvitteringsinfo);

        assertThat(leveringsKvittering.ebmsBekreftbar).isEqualTo(ebmsBekreftbar);
        assertThat(leveringsKvittering.kvitteringsinfo).isEqualTo(kvitteringsinfo);
    }

    @Test
    public void testErKvittering() {
//        EbmsApplikasjonsKvittering applikasjonsKvittering = getEbmsApplikasjonsKvittering("leveringskvitteringStandardBusinessDocument.xml");
//        LeveringsKvittering leveringsKvittering = new LeveringsKvittering(applikasjonsKvittering);
//
//        assertThat(leveringsKvittering.erOkKvittering()).isTrue();
    }

    @Test
    public void getReferanser() {
        getEbmsApplikasjonsKvittering("");

    }

    private EbmsApplikasjonsKvittering getEbmsApplikasjonsKvittering(String fileName) {
        Jaxb2Marshaller marshallerSingleton = Marshalling.getMarshallerSingleton();

        String s = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ns5:Reference xmlns:ns5=\"http://www.w3.org/2000/09/xmldsig#\" URI=\"#id-554d0262-093d-4253-988a-38067575d7cb\" xmlns:ns2=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns3=\"http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader\" xmlns:ns4=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:ns6=\"http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/\" xmlns:ns7=\"http://docs.oasis-open.org/ebxml-bp/ebbp-signals-2.0\" xmlns:ns8=\"http://www.w3.org/1999/xlink\" xmlns:ns9=\"http://begrep.difi.no/sdp/schema_v10\" xmlns:ns10=\"http://uri.etsi.org/2918/v1.2.1#\" xmlns:ns11=\"http://uri.etsi.org/01903/v1.3.2#\"><ns5:Transforms><ns5:Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"><ec:InclusiveNamespaces xmlns:ec=\"http://www.w3.org/2001/10/xml-exc-c14n#\" xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\" PrefixList=\"\"/></ns5:Transform></ns5:Transforms><ns5:DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmlenc#sha256\"/><ns5:DigestValue>5SBnm+H0etMdqMDU+SCeDhAKGtQghKDi4Uan9+fpN1k=</ns5:DigestValue></ns5:Reference>";


//        InputStream systemResourceAsStream = getClass().getResourceAsStream("/xml/response/" + fileName);
        Object unmarshalled = marshallerSingleton.unmarshal(new StreamSource(new StringReader(s)));

        return EbmsApplikasjonsKvittering.create(EbmsAktoer.avsender("984661185"), EbmsAktoer.avsender("988015814"), (StandardBusinessDocument) unmarshalled).build();
    }
}