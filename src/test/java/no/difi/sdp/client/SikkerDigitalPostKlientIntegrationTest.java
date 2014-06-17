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
import no.difi.sdp.client.domain.Noekkelpar;
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
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static no.difi.sdp.client.ObjectMother.createEbmsAapningsKvittering;
import static no.difi.sdp.client.ObjectMother.forsendelse;
import static org.fest.assertions.api.Assertions.assertThat;

public class SikkerDigitalPostKlientIntegrationTest {

    private Sertifikat mottakerSertifikat = ObjectMother.mottakerSertifikat();

    private SikkerDigitalPostKlient postklient;

    private Noekkelpar avsenderNoekkelpar() {
        try {
            String alias = "meldingsformidler";
            String passphrase = "abcd1234";
            String keyStoreFile = "/keystore.jce";

            KeyStore keyStore = KeyStore.getInstance("JCEKS");
            keyStore.load(new ClassPathResource(keyStoreFile).getInputStream(), passphrase.toCharArray());
            return Noekkelpar.fraKeyStore(keyStore, alias, passphrase);
        } catch (Exception e) {
            throw new RuntimeException("Kunne ikke laste nøkkelpar for kjøring av tester. " +
                    "For å kjøre integrasjonstester må det ligge inne et gyldig virksomhetssertifikat for test (med tilhørende certificate chain). " +
                    "Keystore med tilhørende alias og passphrase settes i " + this.getClass().getSimpleName() + ".", e);
        }
    }

    @Before
    public void setUp() {
        KlientKonfigurasjon klientKonfigurasjon = KlientKonfigurasjon.builder()
                .meldingsformidlerRoot("https://qaoffentlig.meldingsformidler.digipost.no/api/ebms")
                .connectionTimeout(20, TimeUnit.SECONDS)
                .build();

        Avsender avsender = ObjectMother.avsenderMedSertifikat(avsenderNoekkelpar());

        postklient = new SikkerDigitalPostKlient(avsender, klientKonfigurasjon);
    }

    @Test
    @Ignore
    public void send_digital_forsendelse() {
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
