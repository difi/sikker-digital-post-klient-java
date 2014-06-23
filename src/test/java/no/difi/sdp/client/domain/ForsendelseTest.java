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
package no.difi.sdp.client.domain;

import no.difi.sdp.client.ObjectMother;
import no.difi.sdp.client.domain.digital_post.DigitalPost;
import no.difi.sdp.client.domain.digital_post.Sikkerhetsnivaa;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import static no.difi.sdp.client.ObjectMother.mottaker;
import static org.fest.assertions.api.Assertions.assertThat;

public class ForsendelseTest {

    private DigitalPost digitalPost;
    private Dokumentpakke dokumentpakke;
    private Forsendelse forsendelse;

    @Before
    public void setup() {
        Mottaker mottaker = mottaker();

        digitalPost = DigitalPost.builder(mottaker, "Denne tittelen er ikke sensitiv")
                .build();

        Dokument hovedDokument = Dokument.builder("Sensitiv brevtittel", "faktura.pdf", new ByteArrayInputStream("hei".getBytes()))
                .mimeType("application/pdf")
                .build();

        dokumentpakke = Dokumentpakke.builder(hovedDokument)
                .vedlegg(new ArrayList<Dokument>())
                .build();

        Behandlingsansvarlig behandlingsansvarlig = ObjectMother.behandlingsansvarlig();

        forsendelse = Forsendelse.digital(behandlingsansvarlig, digitalPost, dokumentpakke)
                .build();
    }

    @Test
    public void test_default_sikkerhetsnivaa_er_satt_til_4() {
        assertThat(forsendelse.getDigitalPost().getSikkerhetsnivaa()).isEqualTo(Sikkerhetsnivaa.NIVAA_4);
    }

    @Test
    public void test_default_prioritet_er_satt_til_NORMAL() {
        assertThat(forsendelse.getPrioritet()).isEqualTo(Prioritet.NORMAL);
    }

    @Test
    public void test_default_konversasjonsId_er_satt() {
        assertThat(forsendelse.getKonversasjonsId()).isNotEmpty();
    }

}
