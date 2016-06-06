package no.difi.sdp.client2.domain.kvittering;

import no.digipost.api.representations.EbmsAktoer;
import no.digipost.api.representations.EbmsApplikasjonsKvittering;
import no.digipost.api.representations.KanBekreftesSomBehandletKvittering;
import no.digipost.api.representations.Referanse;
import no.digipost.api.xml.Marshalling;
import org.junit.Test;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;

import static org.fest.assertions.api.Assertions.assertThat;

import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;

public class ForretningsKvitteringTest {

    @Test
    public void testKonstruktør_initializesProperly() {
        KanBekreftesSomBehandletKvittering kanBekreftesSomBehandletKvittering = new KanBekreftesSomBehandletKvittering() {
            @Override
            public String getMeldingsId() {
                return "MeldingsId";
            }

            @Override
            public Referanse getReferanse() {
                return null;
            }
        };
        Kvitteringsinfo kvitteringsinfo = new Kvitteringsinfo();
        LeveringsKvittering leveringsKvittering = new LeveringsKvittering(kanBekreftesSomBehandletKvittering, kvitteringsinfo);

        assertThat(leveringsKvittering.kanBekreftesSomBehandletKvittering).isEqualTo(kanBekreftesSomBehandletKvittering);
        assertThat(leveringsKvittering.kvitteringsinfo).isEqualTo(kvitteringsinfo);
    }
}