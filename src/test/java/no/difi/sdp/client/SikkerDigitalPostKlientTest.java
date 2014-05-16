package no.difi.sdp.client;

import no.difi.sdp.client.domain.*;
import no.difi.sdp.client.domain.digital_post.DigitalPost;
import no.difi.sdp.client.domain.fysisk_post.FysiskPost;
import no.difi.sdp.client.domain.fysisk_post.NorskPostadresse;
import no.difi.sdp.client.domain.fysisk_post.PostType;
import no.difi.sdp.client.domain.fysisk_post.UtenlandskPostadresse;
import no.difi.sdp.client.domain.digital_post.EpostVarsel;
import no.difi.sdp.client.domain.digital_post.Sikkerhetsnivaa;
import no.difi.sdp.client.domain.digital_post.SmsVarsel;
import org.junit.Test;
import sun.security.x509.X509CertImpl;

import java.io.ByteArrayInputStream;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Date;

import static java.util.Arrays.asList;

public class SikkerDigitalPostKlientTest {

    @Test
    public void test_build_digital_forsendelse() {
        PrivateKey privatnoekkel = null;
        Avsender avsender = Avsender.builder("984661185", Sertifikat.fraX509Certificate(new X509CertImpl()), privatnoekkel)
                .fakturaReferanse("ØK1")
                .avsenderIdentifikator("12345")
                .build();

        SikkerDigitalPostKlient postklient = new SikkerDigitalPostKlient(avsender, new KlientKonfigurasjon());

        EpostVarsel epostVarsel = EpostVarsel.builder("Du har mottatt brev i din digitale postkasse")
                .epostadresse("example@email.org")
                .varselEtterDager(asList(1, 4, 10))
                .build();

        Mottaker mottaker = Mottaker.builder("01129955131", "postkasseadresse", Sertifikat.fraX509Certificate(new X509CertImpl()), "984661185")
                .build();

        SmsVarsel smsVarsel = SmsVarsel.builder("Du har mottatt brev i din digitale postkasse")
                .mobilnummer("4799999999")
                .varselEtterDager(asList(1, 7))
                .spraakkode("SE")
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
                .build();

        postklient.send(forsendelse);


        KvitteringForespoersel kvitteringForespoersel = KvitteringForespoersel.builder(Prioritet.NORMAL).build();

        ForretningsKvittering forretningsKvittering = postklient.hentKvittering(kvitteringForespoersel);

        postklient.bekreftKvittering(forretningsKvittering);
    }

    @Test
    public void test_build_fysisk_forsendelse() {
        PrivateKey privatnoekkel = null;
        Avsender avsender = Avsender.builder("984661185", Sertifikat.fraX509Certificate(new X509CertImpl()), privatnoekkel).build();
        SikkerDigitalPostKlient postklient = new SikkerDigitalPostKlient(avsender, new KlientKonfigurasjon());

        NorskPostadresse norskAdresse = NorskPostadresse.builder("Per Post", "Bedriften AS", "Storgata 15", "0106", "Oslo").build();
        NorskPostadresse returAdresse = NorskPostadresse.builder("Avsender", "Avsenderbedriften AS", "Postboks 15", "2712", "Brandbu").build();
        FysiskPost fysiskPost = FysiskPost.builder("936441114", Sertifikat.fraX509Certificate(new X509CertImpl()), norskAdresse, returAdresse)
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
    public void test_build_fysisk_utenlandsforsendelse() {
        PrivateKey privatnoekkel = null;
        Avsender avsender = Avsender.builder("984661185", Sertifikat.fraX509Certificate(new X509CertImpl()), privatnoekkel).build();
        SikkerDigitalPostKlient postklient = new SikkerDigitalPostKlient(avsender, new KlientKonfigurasjon());

        UtenlandskPostadresse postadresse = UtenlandskPostadresse.builder("Mr. I. K. Taneja", "Flat No. 100", "Triveni Apartments", "Pitam Pura", "NEW DELHI 110034", "India", "IN").build();
        NorskPostadresse returadresse = NorskPostadresse.builder("Avsender", "Avsenderbedriften AS", "Postboks 15", "2712", "Brandbu").build();
        FysiskPost fysiskPost = FysiskPost.builder("936441114", Sertifikat.fraX509Certificate(new X509CertImpl()), postadresse, returadresse)
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
