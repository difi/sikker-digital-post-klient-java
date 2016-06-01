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

public class ForretningsKvitteringTest {

    @Test
    public void testErKvittering() {
        EbmsApplikasjonsKvittering applikasjonsKvittering = getEbmsApplikasjonsKvittering("leveringskvitteringStandardBusinessDocument.xml");
        LeveringsKvittering leveringsKvittering = new LeveringsKvittering(applikasjonsKvittering);

        assertThat(leveringsKvittering.erKvittering()).isTrue();
    }

    @Test
    public void testErFeil() {
        EbmsApplikasjonsKvittering applikasjonsKvittering = getEbmsApplikasjonsKvittering("feiletkvitteringStandardBusinessDocument.xml");
        Feil feiletKvittering = Feil.builder(applikasjonsKvittering, Feil.Feiltype.KLIENT).build();

        assertThat(feiletKvittering.erFeil()).isTrue();

    }

    private EbmsApplikasjonsKvittering getEbmsApplikasjonsKvittering(String fileName) {
        Jaxb2Marshaller marshallerSingleton = Marshalling.getMarshallerSingleton();

        InputStream systemResourceAsStream = getClass().getResourceAsStream("/xml/response/" + fileName);
        Object unmarshalled = marshallerSingleton.unmarshal(new StreamSource(systemResourceAsStream));

        return EbmsApplikasjonsKvittering.create(EbmsAktoer.avsender("984661185"), EbmsAktoer.avsender("988015814"), (StandardBusinessDocument) unmarshalled).build();
    }
}