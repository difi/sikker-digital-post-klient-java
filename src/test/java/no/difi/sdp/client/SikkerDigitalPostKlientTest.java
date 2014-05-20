package no.difi.sdp.client;

import no.difi.sdp.client.domain.*;
import no.difi.sdp.client.domain.digital_post.DigitalPost;
import no.difi.sdp.client.domain.digital_post.EpostVarsel;
import no.difi.sdp.client.domain.digital_post.Sikkerhetsnivaa;
import no.difi.sdp.client.domain.digital_post.SmsVarsel;
import no.difi.sdp.client.domain.fysisk_post.FysiskPost;
import no.difi.sdp.client.domain.fysisk_post.NorskPostadresse;
import no.difi.sdp.client.domain.fysisk_post.PostType;
import no.difi.sdp.client.domain.fysisk_post.UtenlandskPostadresse;
import no.difi.sdp.client.domain.kvittering.KvitteringForespoersel;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Date;

import static java.util.Arrays.asList;

public class SikkerDigitalPostKlientTest {

    private KlientKonfigurasjon klientKonfigurasjon;
    private Sertifikat gyldigSertfikat = Sertifikat.fraBase64X509String("MIIDFDCCAr6gAwIBAgIJALENVFrUMVgcMA0GCSqGSIb3DQEBBQUAMIGQMQswCQYDVQQGEwJOTzENMAsGA1UECBMET3NsbzENMAsGA1UEBxMET3NsbzENMAsGA1UEChMERElGSTENMAsGA1UECxMERElGSTEhMB8GA1UEAxMYU2lra2VyIERpZ2l0YWwgUG9zdCBUZXN0MSIwIAYJKoZIhvcNAQkBFhNkaWZpIGF0IGRpZmkgZG90IG5vMB4XDTE0MDUxNjA4MjQ0MloXDTE0MDYxNTA4MjQ0MlowgZAxCzAJBgNVBAYTAk5PMQ0wCwYDVQQIEwRPc2xvMQ0wCwYDVQQHEwRPc2xvMQ0wCwYDVQQKEwRESUZJMQ0wCwYDVQQLEwRESUZJMSEwHwYDVQQDExhTaWtrZXIgRGlnaXRhbCBQb3N0IFRlc3QxIjAgBgkqhkiG9w0BCQEWE2RpZmkgYXQgZGlmaSBkb3Qgbm8wXDANBgkqhkiG9w0BAQEFAANLADBIAkEA1OteZ0rH+269STIDm2ECmop593A+7v9ih6ydow11wCojGvNnHGjeollzTn+F7caRqLCl7vKr3uttINBFA7E34QIDAQABo4H4MIH1MB0GA1UdDgQWBBRkkkvwgXi/qqQHLyMDttBDCN8PNzCBxQYDVR0jBIG9MIG6gBRkkkvwgXi/qqQHLyMDttBDCN8PN6GBlqSBkzCBkDELMAkGA1UEBhMCTk8xDTALBgNVBAgTBE9zbG8xDTALBgNVBAcTBE9zbG8xDTALBgNVBAoTBERJRkkxDTALBgNVBAsTBERJRkkxITAfBgNVBAMTGFNpa2tlciBEaWdpdGFsIFBvc3QgVGVzdDEiMCAGCSqGSIb3DQEJARYTZGlmaSBhdCBkaWZpIGRvdCBub4IJALENVFrUMVgcMAwGA1UdEwQFMAMBAf8wDQYJKoZIhvcNAQEFBQADQQA43EV/uoAXPEyZSXg+9g/jkxrmhNHeG8evLSM3MqLeS6lO0P6hnGlhoF9GYjqMx7ntYdE8i8jt3a5GRupTIpHB");
    private Noekkelpar avsendersNoekkelpar;

    private SikkerDigitalPostKlient postklient;

    @Before
    public void setUp() {
        klientKonfigurasjon = KlientKonfigurasjon.builder()
                .meldingsformidlerRoot("https://qaoffentlig.meldingsformidler.digipost.no/api/")
                //.meldingsformidlerRoot("http://localhost:8049")
                .build();

        try {
            KeyStore keyStore = KeyStore.getInstance("jks");
            keyStore.load(new ClassPathResource("/avsender-keystore.jks").getInputStream(), "password1234".toCharArray());
            avsendersNoekkelpar = Noekkelpar.fraKeyStore(keyStore, "avsender", "password1234");

            Avsender avsender = Avsender.builder("984661185", avsendersNoekkelpar)
                    .fakturaReferanse("ØK1")
                    .avsenderIdentifikator("12345")
                    .build();

            postklient = new SikkerDigitalPostKlient(avsender, klientKonfigurasjon);
        } catch (Exception e) {
            throw new RuntimeException("Kunne ikke laste keystore", e);
        } 
    }

    @Test
    @Ignore
    public void test_build_digital_forsendelse() {
        EpostVarsel epostVarsel = EpostVarsel.builder("Du har mottatt brev i din digitale postkasse")
                .epostadresse("example@email.org")
                .varselEtterDager(asList(1, 4, 10))
                .build();

        Mottaker mottaker = Mottaker.builder("01129955131", "postkasseadresse", gyldigSertfikat, "984661185")
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

        postklient.hentKvittering(kvitteringForespoersel);
    }

    @Test
    @Ignore
    public void test_bekreft_kvittering() {

    }

    @Test
    @Ignore
    public void test_build_fysisk_forsendelse() {
        Avsender avsender = Avsender.builder("984661185", avsendersNoekkelpar).build();
        SikkerDigitalPostKlient postklient = new SikkerDigitalPostKlient(avsender, klientKonfigurasjon);

        NorskPostadresse norskAdresse = NorskPostadresse.builder("Per Post", "Bedriften AS", "Storgata 15", "0106", "Oslo").build();
        NorskPostadresse returAdresse = NorskPostadresse.builder("Avsender", "Avsenderbedriften AS", "Postboks 15", "2712", "Brandbu").build();
        FysiskPost fysiskPost = FysiskPost.builder("936441114", gyldigSertfikat, norskAdresse, returAdresse)
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
        Avsender avsender = Avsender.builder("984661185", avsendersNoekkelpar).build();
        SikkerDigitalPostKlient postklient = new SikkerDigitalPostKlient(avsender, klientKonfigurasjon);

        UtenlandskPostadresse postadresse = UtenlandskPostadresse.builder("Mr. I. K. Taneja", "Flat No. 100", "Triveni Apartments", "Pitam Pura", "NEW DELHI 110034", "India", "IN").build();
        NorskPostadresse returadresse = NorskPostadresse.builder("Avsender", "Avsenderbedriften AS", "Postboks 15", "2712", "Brandbu").build();
        FysiskPost fysiskPost = FysiskPost.builder("936441114", gyldigSertfikat, postadresse, returadresse)
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
