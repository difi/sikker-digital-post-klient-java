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
package no.difi.sdp.client2.internal;

import no.difi.sdp.client2.ObjectMother;
import no.difi.sdp.client2.domain.Behandlingsansvarlig;
import no.difi.sdp.client2.domain.TekniskAvsender;
import no.difi.sdp.client2.domain.Dokument;
import no.difi.sdp.client2.domain.Dokumentpakke;
import no.difi.sdp.client2.domain.Forsendelse;
import no.difi.sdp.client2.domain.Mottaker;
import no.difi.sdp.client2.domain.Prioritet;
import no.difi.sdp.client2.domain.digital_post.DigitalPost;
import no.digipost.api.representations.EbmsForsendelse;
import no.digipost.api.representations.EbmsOutgoingMessage;
import no.digipost.api.representations.Organisasjonsnummer;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;

import static no.difi.sdp.client2.ObjectMother.mottakerSertifikat;
import static org.fest.assertions.api.Assertions.assertThat;

public class EbmsForsendelseBuilderTest {

    private EbmsForsendelseBuilder sut;

    @Before
    public void setUp() {
        sut = new EbmsForsendelseBuilder();
    }

    @Test
    public void bygg_minimalt_request() {
        TekniskAvsender avsender = TekniskAvsender.builder("991825827", ObjectMother.noekkelpar()).build();
        Mottaker mottaker = Mottaker.builder("01129955131", "postkasseadresse", mottakerSertifikat(), "984661185").build();
        DigitalPost digitalpost = DigitalPost.builder(mottaker, "Ikke-sensitiv tittel").build();
        Dokument dokument = Dokument.builder("Sensitiv tittel", "filnavn", new ByteArrayInputStream("hei".getBytes())).build();
        Dokumentpakke dokumentpakke = Dokumentpakke.builder(dokument).build();
        Behandlingsansvarlig behandlingsansvarlig = Behandlingsansvarlig.builder("936796702").build();
        Forsendelse forsendelse = Forsendelse.digital(behandlingsansvarlig, digitalpost, dokumentpakke).build();

        EbmsForsendelse ebmsForsendelse = sut.buildEbmsForsendelse(avsender, new Organisasjonsnummer("984661185"), forsendelse);

        // Hovedpoenget her er at det henger sammen å bygge et request uten optional-felter satt (at vi ikke får NullPointerException).
        assertThat(ebmsForsendelse.getAvsender().orgnr.asIso6523()).isEqualTo("9908:991825827");
        assertThat(ebmsForsendelse.getDokumentpakke().getContentType()).isEqualTo("application/cms");
    }

    @Test
    public void korrekt_mpc() {
        // Mpc består av prioritet og mpc-id som brukes til å skille mellom forskjellige MPC-køer hos samme avsender
        TekniskAvsender avsender = TekniskAvsender.builder("991825827", ObjectMother.noekkelpar()).build();
        Mottaker mottaker = Mottaker.builder("01129955131", "postkasseadresse", mottakerSertifikat(), "984661185").build();
        DigitalPost digitalpost = DigitalPost.builder(mottaker, "Ikke-sensitiv tittel").build();
        Behandlingsansvarlig behandlingsansvarlig = Behandlingsansvarlig.builder("991825827").build();
        Forsendelse forsendelse = Forsendelse.digital(behandlingsansvarlig, digitalpost, ObjectMother.dokumentpakke()).mpcId("mpcId").prioritet(Prioritet.PRIORITERT).build();

        EbmsForsendelse ebmsForsendelse = sut.buildEbmsForsendelse(avsender, new Organisasjonsnummer("984661185"), forsendelse);

        assertThat(ebmsForsendelse.prioritet).isEqualTo(EbmsOutgoingMessage.Prioritet.PRIORITERT);
        assertThat(ebmsForsendelse.mpcId).isEqualTo("mpcId");
    }

}