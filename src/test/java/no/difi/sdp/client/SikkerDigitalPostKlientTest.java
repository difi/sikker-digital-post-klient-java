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
package no.difi.sdp.client;

import no.difi.sdp.client.domain.Avsender;
import no.difi.sdp.client.domain.Dokument;
import no.difi.sdp.client.domain.Dokumentpakke;
import no.difi.sdp.client.domain.Forsendelse;
import no.difi.sdp.client.domain.Prioritet;
import no.difi.sdp.client.domain.Sertifikat;
import no.difi.sdp.client.domain.fysisk_post.FysiskPost;
import no.difi.sdp.client.domain.fysisk_post.NorskPostadresse;
import no.difi.sdp.client.domain.fysisk_post.PostType;
import no.difi.sdp.client.domain.fysisk_post.UtenlandskPostadresse;
import no.difi.sdp.client.domain.kvittering.AapningsKvittering;
import no.difi.sdp.client.domain.kvittering.ForretningsKvittering;
import no.difi.sdp.client.domain.kvittering.KvitteringForespoersel;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static no.difi.sdp.client.ObjectMother.createEbmsAapningsKvittering;
import static no.difi.sdp.client.ObjectMother.forsendelse;
import static no.difi.sdp.client.domain.exceptions.TransportException.AntattSkyldig.UKJENT;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Fail.fail;

public class SikkerDigitalPostKlientTest {

    private Sertifikat mottakerSertifikat = ObjectMother.mottakerSertifikat();

    private SikkerDigitalPostKlient postklient;

    static {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        Logger.getLogger("org.jcp.xml.dsig").setLevel(Level.INFO);
        Logger.getLogger("com.sun.org.apache.xml.internal.security").setLevel(Level.INFO);
        SLF4JBridgeHandler.install();
    }

    @Before
    public void setUp() {
        KlientKonfigurasjon klientKonfigurasjon = KlientKonfigurasjon.builder()
                .meldingsformidlerRoot("https://10.255.255.1/api/ebms")
                .connectionTimeout(20, TimeUnit.SECONDS)
                .build();

        //todo: bytte ut med et gyldig Buypass sertifikat når vi har det på plass
        Avsender avsender = ObjectMother.avsenderMedBuypassSertifikat();

        postklient = new SikkerDigitalPostKlient(avsender, klientKonfigurasjon);
    }

    @Test
    public void haandter_connection_timeouts() {
        String lokalTimeoutUrl = "http://10.255.255.1/";
        KlientKonfigurasjon klientKonfigurasjon = KlientKonfigurasjon.builder()
                .meldingsformidlerRoot(lokalTimeoutUrl)
                .connectionTimeout(1, TimeUnit.MILLISECONDS)
                .build();

        postklient = new SikkerDigitalPostKlient(ObjectMother.avsender(), klientKonfigurasjon);

        try {
            postklient.send(forsendelse());
            fail("Should fail");
        }
        catch (TransportIOException e) {
            assertThat(e.getAntattSkyldig()).isEqualTo(UKJENT);
        }
    }

    @Test
    @Ignore
    public void test_build_digital_forsendelse() {
        Forsendelse forsendelse = forsendelse();

        postklient.send(forsendelse);
    }

    @Test
    @Ignore
    public void test_hent_kvittering() {
        KvitteringForespoersel kvitteringForespoersel = KvitteringForespoersel.builder(Prioritet.NORMAL).build();

        ForretningsKvittering forretningsKvittering = postklient.hentKvittering(kvitteringForespoersel);
        if (forretningsKvittering != null) {
            assertThat(forretningsKvittering.getKonversasjonsId()).isNotEmpty();
            assertThat(forretningsKvittering.getRefToMessageId()).isNotEmpty();
            assertThat(forretningsKvittering.getTidspunkt()).isNotNull();
        }
    }

    @Test
    @Ignore
    public void test_hent_kvittering_og_bekreft_forrige() {
        KvitteringForespoersel kvitteringForespoersel = KvitteringForespoersel.builder(Prioritet.NORMAL).build();
        ForretningsKvittering forrigeKvittering = AapningsKvittering.builder(createEbmsAapningsKvittering()).build();

        ForretningsKvittering forretningsKvittering = postklient.hentKvitteringOgBekreftForrige(kvitteringForespoersel, forrigeKvittering);
        if (forretningsKvittering != null) {
            assertThat(forretningsKvittering.getKonversasjonsId()).isNotEmpty();
            assertThat(forretningsKvittering.getMessageId()).isNotEmpty();
            assertThat(forretningsKvittering.getRefToMessageId()).isNotEmpty();
            assertThat(forretningsKvittering.getTidspunkt()).isNotNull();
        }
    }

    @Test
    @Ignore
    public void test_bekreft_kvittering() {
        ForretningsKvittering forrigeKvittering = AapningsKvittering.builder(createEbmsAapningsKvittering()).build();
        postklient.bekreft(forrigeKvittering);
    }

    @Test
    @Ignore
    public void test_build_fysisk_forsendelse() {
        NorskPostadresse norskAdresse = NorskPostadresse.builder("Per Post", "Bedriften AS", "Storgata 15", "0106", "Oslo").build();
        NorskPostadresse returAdresse = NorskPostadresse.builder("Avsender", "Avsenderbedriften AS", "Postboks 15", "2712", "Brandbu").build();
        FysiskPost fysiskPost = FysiskPost.builder("936441114", mottakerSertifikat, norskAdresse, returAdresse)
                .postType(PostType.A_POST)
                .build();

        Dokument hovedDokument = Dokument.builder("Sensitiv brevtittel", "faktura.pdf", new ByteArrayInputStream("hei".getBytes()))
                .mimeType("application/pdf")
                .build();

        Dokumentpakke dokumentpakke = Dokumentpakke.builder(hovedDokument)
                .vedlegg(new ArrayList<Dokument>())
                .build();

        Forsendelse forsendelse = Forsendelse.fysisk(fysiskPost, dokumentpakke)
                .konversasjonsId("konversasjonsId")
                .prioritet(Prioritet.NORMAL)
                .build();

        postklient.send(forsendelse);


        KvitteringForespoersel kvitteringForespoersel = KvitteringForespoersel.builder(Prioritet.NORMAL).build();
        postklient.hentKvittering(kvitteringForespoersel);
    }

    @Test
    @Ignore
    public void test_build_fysisk_utenlandsforsendelse() {
        UtenlandskPostadresse postadresse = UtenlandskPostadresse.builder("Mr. I. K. Taneja", "Flat No. 100", "Triveni Apartments", "Pitam Pura", "NEW DELHI 110034", "India", "IN").build();
        NorskPostadresse returadresse = NorskPostadresse.builder("Avsender", "Avsenderbedriften AS", "Postboks 15", "2712", "Brandbu").build();
        FysiskPost fysiskPost = FysiskPost.builder("936441114", mottakerSertifikat, postadresse, returadresse)
                .postType(PostType.A_POST)
                .build();

        Dokument hovedDokument = Dokument.builder("Sensitiv brevtittel", "faktura.pdf", new ByteArrayInputStream("hei".getBytes()))
                .mimeType("application/pdf")
                .build();

        Dokumentpakke dokumentpakke = Dokumentpakke.builder(hovedDokument)
                .vedlegg(new ArrayList<Dokument>())
                .build();

        Forsendelse forsendelse = Forsendelse.fysisk(fysiskPost, dokumentpakke)
                .konversasjonsId("konversasjonsId")
                .prioritet(Prioritet.NORMAL)
                .build();

        postklient.send(forsendelse);


        KvitteringForespoersel kvitteringForespoersel = KvitteringForespoersel.builder(Prioritet.NORMAL).build();
        postklient.hentKvittering(kvitteringForespoersel);
    }

}
