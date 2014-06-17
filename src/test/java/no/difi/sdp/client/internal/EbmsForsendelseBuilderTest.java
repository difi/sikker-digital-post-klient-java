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

import no.difi.sdp.client.ObjectMother;
import no.difi.sdp.client.domain.Avsender;
import no.difi.sdp.client.domain.Dokument;
import no.difi.sdp.client.domain.Dokumentpakke;
import no.difi.sdp.client.domain.Forsendelse;
import no.difi.sdp.client.domain.Mottaker;
import no.difi.sdp.client.domain.digital_post.DigitalPost;
import no.digipost.api.representations.EbmsForsendelse;
import no.digipost.api.representations.Organisasjonsnummer;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;

import static no.difi.sdp.client.ObjectMother.mottakerSertifikat;
import static org.fest.assertions.api.Assertions.assertThat;

public class EbmsForsendelseBuilderTest {

    private EbmsForsendelseBuilder sut;

    @Before
    public void setUp() {
        sut = new EbmsForsendelseBuilder();
    }

    @Test
    public void bygg_minimalt_request() {
        Avsender avsender = Avsender.builder("991825827", ObjectMother.noekkelpar()).build();
        Mottaker mottaker = Mottaker.builder("01129955131", "postkasseadresse", mottakerSertifikat(), "984661185").build();
        DigitalPost digitalpost = DigitalPost.builder(mottaker, "Ikke-sensitiv tittel").build();
        Dokument dokument = Dokument.builder("Sensitiv tittel", "filnavn", new ByteArrayInputStream("hei".getBytes())).build();
        Dokumentpakke dokumentpakke = Dokumentpakke.builder(dokument).build();
        Forsendelse forsendelse = Forsendelse.digital(digitalpost, dokumentpakke).build();

        EbmsForsendelse ebmsForsendelse = sut.buildEbmsForsendelse(avsender, new Organisasjonsnummer("984661185"), forsendelse);

        // Hovedpoenget her er at det henger sammen å bygge et request uten optional-felter satt (at vi ikke får NullPointerException).
        assertThat(ebmsForsendelse.getAvsender().orgnr.asIso6523()).isEqualTo("9908:991825827");
        assertThat(ebmsForsendelse.getDokumentpakke().getContentType()).isEqualTo("application/cms");
    }

}