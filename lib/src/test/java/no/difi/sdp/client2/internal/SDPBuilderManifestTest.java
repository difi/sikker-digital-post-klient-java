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
import no.digipost.api.xml.JaxbMarshaller;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;
import static no.difi.sdp.client2.ObjectMother.mottakerSertifikat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToCompressingWhiteSpace;

class SDPBuilderManifestTest {

    private static final JaxbMarshaller marshaller = JaxbMarshaller.marshallerForClasses(asList(SDPManifest.class));

    private SDPBuilder sdpBuilder;

    @BeforeEach
    void set_up() throws Exception {
        sdpBuilder = new SDPBuilder();
    }

    @Test
    void build_expected_manifest() throws Exception {
        String expectedXml = IOUtils.toString(this.getClass().getResourceAsStream("/asic/expected-asic-manifest.xml"), UTF_8).replaceAll(">\\s*", ">");

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

        String xml = marshaller.marshalToString(manifest);
        assertThat(xml, equalToCompressingWhiteSpace(expectedXml));
    }

}
