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
package no.difi.sdp.client2.domain;

import no.difi.begrep.sdp.schema_v10.SDPDigitalPost;
import no.difi.sdp.client2.ObjectMother;
import no.difi.sdp.client2.domain.digital_post.DigitalPost;
import no.difi.sdp.client2.domain.digital_post.Sikkerhetsnivaa;
import no.difi.sdp.client2.domain.fysisk_post.FysiskPost;
import no.difi.sdp.client2.domain.fysisk_post.KonvoluttAdresse;
import no.difi.sdp.client2.domain.fysisk_post.Landkoder;
import no.difi.sdp.client2.domain.fysisk_post.Posttype;
import no.difi.sdp.client2.domain.fysisk_post.Returhaandtering;
import no.difi.sdp.client2.domain.fysisk_post.Utskriftsfarge;
import no.difi.sdp.client2.internal.SDPBuilder;
import no.digipost.api.representations.Organisasjonsnummer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import static no.difi.sdp.client2.ObjectMother.mottaker;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

public class ForsendelseTest {

    private DigitalPost digitalPost;
    private Dokumentpakke dokumentpakke;
    private Forsendelse forsendelse;

    @BeforeEach
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

        Avsender avsender = ObjectMother.avsender();

        forsendelse = Forsendelse.digital(avsender, digitalPost, dokumentpakke)
                .build();
    }

    @Test
    public void default_sikkerhetsnivaa_er_satt_til_4() {
        assertThat(forsendelse.getDigitalPost().getSikkerhetsnivaa(), equalTo(Sikkerhetsnivaa.NIVAA_4));
    }

    @Test
    public void default_prioritet_er_satt_til_NORMAL() {
        assertThat(forsendelse.getPrioritet(), equalTo(Prioritet.NORMAL));
    }

    @Test
    public void default_konversasjonsId_er_satt() {
        assertThat(forsendelse.getKonversasjonsId(), notNullValue());
    }

    @Test
    public void fysisk_post_faar_land_eller_landkode() {
        FysiskPost adresse = FysiskPost.builder()
                .adresse(KonvoluttAdresse.build("Rall").iUtlandet("Sweden Main Street", null, null, null, Landkoder.Predefinert.SVERIGE).build())
                .retur(Returhaandtering.MAKULERING_MED_MELDING, KonvoluttAdresse.build("Rall").iUtlandet("Hungary street 2", null, null, null, "Ungarn").build())
                .sendesMed(Posttype.A_PRIORITERT)
                .utskrift(Utskriftsfarge.FARGE, new TekniskMottaker(Organisasjonsnummer.of("988015814"), null)).build();
        Forsendelse fysiskForsendelse = Forsendelse.fysisk(ObjectMother.avsender(), adresse,
                Dokumentpakke.builder(Dokument.builder("Sensitiv brevtittel", "faktura.pdf", new ByteArrayInputStream("hei".getBytes())).build()).build()).build();

        assertThat(fysiskForsendelse.type, equalTo(Forsendelse.Type.FYSISK));
        assertThat(fysiskForsendelse.getFysiskPost().getAdresse().getLandkode(), equalTo("SE"));
        assertThat(fysiskForsendelse.getFysiskPost().getAdresse().getLand(), is(nullValue()));

        assertThat(fysiskForsendelse.getFysiskPost().getReturadresse().getLand(), equalTo("Ungarn"));
        assertThat(fysiskForsendelse.getFysiskPost().getReturadresse().getLandkode(), is(nullValue()));

        SDPDigitalPost sdpDigitalPost = new SDPBuilder().buildDigitalPost(fysiskForsendelse);
        assertThat(sdpDigitalPost.getFysiskPostInfo().getMottaker().getUtenlandskAdresse().getLand(), is(nullValue()));
        assertThat(sdpDigitalPost.getFysiskPostInfo().getMottaker().getUtenlandskAdresse().getLandkode(), equalTo("SE"));

        assertThat(sdpDigitalPost.getFysiskPostInfo().getRetur().getMottaker().getUtenlandskAdresse().getLandkode(), is(nullValue()));
        assertThat(sdpDigitalPost.getFysiskPostInfo().getRetur().getMottaker().getUtenlandskAdresse().getLand(), equalTo("Ungarn"));
    }
}
