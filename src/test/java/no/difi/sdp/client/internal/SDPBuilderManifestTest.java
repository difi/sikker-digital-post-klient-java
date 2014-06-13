/**
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
package no.difi.sdp.client.internal;

import no.difi.begrep.sdp.schema_v10.SDPManifest;
import no.difi.sdp.client.ObjectMother;
import no.difi.sdp.client.domain.Avsender;
import no.difi.sdp.client.domain.Dokument;
import no.difi.sdp.client.domain.Dokumentpakke;
import no.difi.sdp.client.domain.Forsendelse;
import no.difi.sdp.client.domain.Mottaker;
import no.difi.sdp.client.domain.digital_post.DigitalPost;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.xml.transform.StringResult;

import javax.xml.bind.Marshaller;
import java.io.ByteArrayInputStream;
import java.util.Collections;

import static org.fest.assertions.api.Assertions.assertThat;

public class SDPBuilderManifestTest {

    private static final Jaxb2Marshaller marshaller;

    static {
        marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(SDPManifest.class);
        marshaller.setMarshallerProperties(Collections.singletonMap(Marshaller.JAXB_FORMATTED_OUTPUT, true));
    }

    private SDPBuilder sut;

    @Before
    public void setUp() throws Exception {
        sut = new SDPBuilder();
    }

    @Test
    public void build_expected_manifest() throws Exception {
        String expectedXml = IOUtils.toString(this.getClass().getResourceAsStream("/asic/expected-asic-manifest.xml"));

        Avsender avsender = Avsender.builder("123456789", null).fakturaReferanse("ØK1").avsenderIdentifikator("0123456789").build();

        Mottaker mottaker = Mottaker.builder("11077941012", "123456", ObjectMother.mottakerSertifikat(), "984661185").build();

        Forsendelse forsendelse = Forsendelse.digital(DigitalPost.builder(mottaker, "Ikke sensitiv tittel").build(),
                Dokumentpakke.builder(Dokument.builder("Vedtak", "vedtak_2398324.pdf", new ByteArrayInputStream("vedtak".getBytes())).mimeType("application/pdf").build()).
                        vedlegg(
                                Dokument.builder("informasjon", "info.html", new ByteArrayInputStream("info".getBytes())).mimeType("text/html").build(),
                                Dokument.builder("journal", "journal.txt", new ByteArrayInputStream("journal".getBytes())).mimeType("text/plain").build())
                .build()).build();

        SDPManifest manifest = sut.createManifest(avsender, forsendelse);

        StringResult xmlResult = new StringResult();
        marshaller.marshal(manifest, xmlResult);

        assertThat(xmlResult.toString()).isEqualTo(expectedXml);
    }

}