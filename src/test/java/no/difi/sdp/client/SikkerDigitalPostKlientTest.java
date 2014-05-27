package no.difi.sdp.client;

import no.difi.sdp.client.domain.Avsender;
import no.difi.sdp.client.domain.Dokument;
import no.difi.sdp.client.domain.Dokumentpakke;
import no.difi.sdp.client.domain.Forsendelse;
import no.difi.sdp.client.domain.Mottaker;
import no.difi.sdp.client.domain.Prioritet;
import no.difi.sdp.client.domain.Sertifikat;
import no.difi.sdp.client.domain.digital_post.DigitalPost;
import no.difi.sdp.client.domain.digital_post.EpostVarsel;
import no.difi.sdp.client.domain.digital_post.Sikkerhetsnivaa;
import no.difi.sdp.client.domain.digital_post.SmsVarsel;
import no.difi.sdp.client.domain.fysisk_post.FysiskPost;
import no.difi.sdp.client.domain.fysisk_post.NorskPostadresse;
import no.difi.sdp.client.domain.fysisk_post.PostType;
import no.difi.sdp.client.domain.fysisk_post.UtenlandskPostadresse;
import no.difi.sdp.client.domain.kvittering.ForretningsKvittering;
import no.difi.sdp.client.domain.kvittering.KvitteringForespoersel;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Date;

import static java.util.Arrays.asList;
import static org.fest.assertions.api.Assertions.assertThat;

public class SikkerDigitalPostKlientTest {

    private Sertifikat mottakerSertifikat = ObjectMother.mottakerSertifikat();

    private SikkerDigitalPostKlient postklient;

    @Before
    public void setUp() {
        KlientKonfigurasjon klientKonfigurasjon = KlientKonfigurasjon.builder()
                //.meldingsformidlerRoot("https://qaoffentlig.meldingsformidler.digipost.no/api/")
                .meldingsformidlerRoot("http://localhost:8049")
                .build();

        //todo: bytte ut med et gyldig Bypass sertifikat når vi har det på plass
        Avsender avsender = ObjectMother.avsenderMedBypassSertifikat();

        postklient = new SikkerDigitalPostKlient(avsender, klientKonfigurasjon);
    }

    @Test
    @Ignore
    public void test_build_digital_forsendelse() {
        EpostVarsel epostVarsel = EpostVarsel.builder("Du har mottatt brev i din digitale postkasse")
                .epostadresse("example@email.org")
                .varselEtterDager(asList(1, 4, 10))
                .build();

        Mottaker mottaker = Mottaker.builder("01129955131", "postkasseadresse", mottakerSertifikat, "984661185")
                .build();

        SmsVarsel smsVarsel = SmsVarsel.builder("Du har mottatt brev i din digitale postkasse")
                .mobilnummer("4799999999")
                .varselEtterDager(asList(1, 7))
                .build();

        DigitalPost digitalPost = DigitalPost.builder(mottaker, "Ikke-sensitiv tittel for forsendelsen")
                .virkningsdato(new Date())
                .aapningskvittering(false)
                .sikkerhetsnivaa(Sikkerhetsnivaa.NIVAA_3)
                .epostVarsel(epostVarsel)
                .smsVarsel(smsVarsel)
                .build();

        Dokument hovedDokument = Dokument.builder("Sensitiv brevtittel", "faktura.pdf", new ByteArrayInputStream("hei".getBytes()))
                .mimeType("application/pdf")
                .build();

        Dokumentpakke dokumentpakke = Dokumentpakke.builder(hovedDokument)
                .vedlegg(new ArrayList<Dokument>())
                .build();

        Forsendelse forsendelse = Forsendelse.builder(digitalPost, dokumentpakke)
                .konversasjonsId("konversasjonsId")
                .prioritet(Prioritet.NORMAL)
                .spraakkode("NO")
                .build();

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
    public void test_bekreft_kvittering() {

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

        Forsendelse forsendelse = Forsendelse.builder(fysiskPost, dokumentpakke)
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

        Forsendelse forsendelse = Forsendelse.builder(fysiskPost, dokumentpakke)
                .konversasjonsId("konversasjonsId")
                .prioritet(Prioritet.NORMAL)
                .build();

        postklient.send(forsendelse);


        KvitteringForespoersel kvitteringForespoersel = KvitteringForespoersel.builder(Prioritet.NORMAL).build();
        postklient.hentKvittering(kvitteringForespoersel);
    }

}
