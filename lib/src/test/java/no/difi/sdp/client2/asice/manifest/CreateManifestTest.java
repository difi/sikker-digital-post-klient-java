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
package no.difi.sdp.client2.asice.manifest;

import no.difi.sdp.client2.ObjectMother;
import no.difi.sdp.client2.domain.Avsender;
import no.difi.sdp.client2.domain.Forsendelse;
import no.difi.sdp.client2.domain.Mottaker;
import no.difi.sdp.client2.domain.digital_post.DigitalPost;
import no.difi.sdp.client2.domain.exceptions.XmlValideringException;
import no.digipost.api.representations.Organisasjonsnummer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.nio.charset.StandardCharsets.UTF_8;
import static no.difi.sdp.client2.ObjectMother.mottakerSertifikat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CreateManifestTest {

    private CreateManifest sut;

    @BeforeEach
    public void set_up() throws Exception {
        sut = new CreateManifest();
    }

    @Test
    public void accept_valid_forsendelse() {
        Forsendelse forsendelse = ObjectMother.forsendelse();

        Manifest manifest = sut.createManifest(forsendelse);
        String manifestXml = new String(manifest.getBytes(), UTF_8);
        assertAll(
                () -> assertThat(manifest.getFileName(), is("manifest.xml")),
                () -> assertThat(manifestXml, containsString("href=\"faktura.pdf\"")));
    }

    @Test
    public void should_validate_manifest() {
        Mottaker mottaker = Mottaker.builder("04036125433", null, mottakerSertifikat(), Organisasjonsnummer.of("984661185")).build();
        Avsender avsender = Avsender.builder(ObjectMother.avsenderOrganisasjonsnummer()).build();

        Forsendelse ugyldigForsendelse = Forsendelse.digital(avsender, DigitalPost.builder(mottaker, "tittel").build(), ObjectMother.dokumentpakke()).build();
        assertThrows(XmlValideringException.class, () -> sut.createManifest(ugyldigForsendelse));
    }
}