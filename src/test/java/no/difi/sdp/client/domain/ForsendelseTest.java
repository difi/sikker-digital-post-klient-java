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

import no.difi.begrep.sdp.schema_v10.SDPDigitalPost;
import no.difi.sdp.client.ObjectMother;
import no.difi.sdp.client.domain.digital_post.DigitalPost;
import no.difi.sdp.client.domain.digital_post.Sikkerhetsnivaa;
import no.difi.sdp.client.domain.fysisk_post.*;
import no.difi.sdp.client.internal.SDPBuilder;
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

    @Test
	public void fysisk_post_faar_land_eller_landkode() {
    	FysiskPost adresse = FysiskPost.builder()
    			.adresse(KonvoluttAdresse.build("Rall").iUtlandet("Sweden Main Street", null, null, null, Landkoder.Predefinert.SVERIGE).build())
    			.retur(Returhaandtering.MAKULERING_MED_MELDING, KonvoluttAdresse.build("Rall").iUtlandet("Hungary street 2", null, null, null, "Ungarn").build())
    			.sendesMed(Posttype.A_PRIORITERT)
    			.utskrift(Utskriftsfarge.FARGE, new TekniskMottaker("orgnr", null)).build();
		Forsendelse fysiskForsendelse = Forsendelse.fysisk(ObjectMother.behandlingsansvarlig(), adresse,
    			Dokumentpakke.builder(Dokument.builder("Sensitiv brevtittel", "faktura.pdf", new ByteArrayInputStream("hei".getBytes())).build()).build()).build();

		assertThat(fysiskForsendelse.type).isEqualTo(Forsendelse.Type.FYSISK);
		assertThat(fysiskForsendelse.getFysiskPost().getAdresse().getLandkode()).isEqualTo("SE");
		assertThat(fysiskForsendelse.getFysiskPost().getAdresse().getLand()).isNull();

		assertThat(fysiskForsendelse.getFysiskPost().getReturadresse().getLand()).isEqualTo("Ungarn");
		assertThat(fysiskForsendelse.getFysiskPost().getReturadresse().getLandkode()).isNull();;

		SDPDigitalPost sdpDigitalPost = new SDPBuilder().buildDigitalPost(fysiskForsendelse);
		assertThat(sdpDigitalPost.getFysiskPostInfo().getMottaker().getUtenlandskAdresse().getLand()).isNull();
		assertThat(sdpDigitalPost.getFysiskPostInfo().getMottaker().getUtenlandskAdresse().getLandkode()).isEqualTo("SE");

		assertThat(sdpDigitalPost.getFysiskPostInfo().getRetur().getMottaker().getUtenlandskAdresse().getLandkode()).isNull();
		assertThat(sdpDigitalPost.getFysiskPostInfo().getRetur().getMottaker().getUtenlandskAdresse().getLand()).isEqualTo("Ungarn");

	}

}
