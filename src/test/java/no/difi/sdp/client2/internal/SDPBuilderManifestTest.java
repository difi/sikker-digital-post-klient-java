package no.difi.sdp.client2.internal;

import no.difi.begrep.sdp.schema_v10.SDPManifest;
import no.difi.sdp.client2.domain.AktoerOrganisasjonsnummer;
import no.difi.sdp.client2.domain.Avsender;
import no.difi.sdp.client2.domain.Dokument;
import no.difi.sdp.client2.domain.Dokumentpakke;
import no.difi.sdp.client2.domain.Forsendelse;
import no.difi.sdp.client2.domain.MetadataDokument;
import no.difi.sdp.client2.domain.Mottaker;
import no.difi.sdp.client2.domain.digital_post.DigitalPost;
import no.digipost.api.representations.Organisasjonsnummer;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.xml.bind.Marshaller;
import javax.xml.transform.stream.StreamResult;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collections;

import static java.nio.charset.StandardCharsets.UTF_8;
import static no.difi.sdp.client2.ObjectMother.mottakerSertifikat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToCompressingWhiteSpace;

public class SDPBuilderManifestTest {

    private static final Jaxb2Marshaller marshaller;

    static {
        marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(SDPManifest.class);
        marshaller.setMarshallerProperties(Collections.singletonMap(Marshaller.JAXB_FORMATTED_OUTPUT, true));
    }

    private SDPBuilder sdpBuilder;

    @BeforeEach
    public void set_up() throws Exception {
        sdpBuilder = new SDPBuilder();
    }

    @Test
    public void build_expected_manifest() throws Exception {
        String expectedXml = IOUtils.toString(this.getClass().getResourceAsStream("/asic/expected-asic-manifest.xml"), UTF_8);

        Avsender avsender = Avsender.builder(AktoerOrganisasjonsnummer.of("123456789").forfremTilAvsender()).fakturaReferanse("ØK1").avsenderIdentifikator("0123456789").build();

        Mottaker mottaker = Mottaker.builder("11077941012", "123456", mottakerSertifikat(), Organisasjonsnummer.of("984661185")).build();

        Forsendelse forsendelse = Forsendelse.digital(avsender,
                DigitalPost.builder(mottaker, "Ikke sensitiv tittel").build(),
                Dokumentpakke.builder(Dokument.builder("Vedtak", "vedtak_2398324.pdf", new ByteArrayInputStream("vedtak".getBytes())).mimeType("application/pdf").metadataDocument(new MetadataDokument("lenke.xml", "application/vnd.difi.dpi.lenke+xml", "<lenke></lenke".getBytes())).build()).
                        vedlegg(
                                Dokument.builder("informasjon", "info.html", new ByteArrayInputStream("info".getBytes())).mimeType("text/html").build(),
                                Dokument.builder("journal", "journal.txt", new ByteArrayInputStream("journal".getBytes())).mimeType("text/plain").build())
                        .build())
                .build();

        SDPManifest manifest = sdpBuilder.createManifest(forsendelse);

        ByteArrayOutputStream xmlBytes = new ByteArrayOutputStream();
        marshaller.marshal(manifest, new StreamResult(xmlBytes));

        assertThat(xmlBytes.toString("UTF-8"), equalToCompressingWhiteSpace(expectedXml));
    }

}