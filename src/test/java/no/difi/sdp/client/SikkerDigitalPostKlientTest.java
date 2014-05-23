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
import no.difi.sdp.client.domain.kvittering.KvitteringForespoersel;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Date;

import static java.util.Arrays.asList;

public class SikkerDigitalPostKlientTest {

    private Sertifikat mottakerSertifikat = Sertifikat.fraBase64X509String("MIIDfzCCAmegAwIBAgIEAN3XETANBgkqhkiG9w0BAQsFADBwMQswCQYDVQQGEwJOTzENMAsGA1UECBMET3NsbzENMAsGA1UEBxMET3NsbzENMAsGA1UEChMERGlmaTENMAsGA1UECxMERGlmaTElMCMGA1UEAxMcU2lra2VyIERpZ2l0YWwgUG9zdCBtb3R0YWtlcjAeFw0xNDA1MjMxMTM4MjBaFw0yNDA1MjAxMTM4MjBaMHAxCzAJBgNVBAYTAk5PMQ0wCwYDVQQIEwRPc2xvMQ0wCwYDVQQHEwRPc2xvMQ0wCwYDVQQKEwREaWZpMQ0wCwYDVQQLEwREaWZpMSUwIwYDVQQDExxTaWtrZXIgRGlnaXRhbCBQb3N0IG1vdHRha2VyMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAl7FSwdLRSoHKHybxQmlUT9aX7mrqjkNKCEaRzF1w8lswEDK/j3Jmmj4I52HZgsjwobwEsGDA+828Mm+5KOgPVqhzUA7zQmVetaGzkKaE3JS8bcy4tTsrAbbf4N9lBbF6JrbCCUq25sTIkAqyXzCrNaXvtxah2K+8bOIiu8VgsHFNXest9MTxhiomx7dWk3kc/o/pb59S21+/VaM3j9oWUJ+wwkXVJTEuziN1fPYvRSoSKf+Qryx2oAAqanGYvtIBFYAMd9mgC9canMZtnEYUHXaykmLjOvR682P75hmDWNfjLbiB+uyrpzB2H+zuPX75utC40qlN/CFzwU6UtJWQxQIDAQABoyEwHzAdBgNVHQ4EFgQUeCR1OtLrQP5y4rwVJT7dnLrrc5EwDQYJKoZIhvcNAQELBQADggEBAG4sbgwgcxO2CuP2u2WGS85UXH9QOYUqU/IxvHQDgZPUlkVgn4tbouYGrBCNuWWM2F20n29dP32keDVY4s5HoF3aqwuray7zE194q/rkyqDQBaOMCiSALZU4ttKZcrsnxEYTnuVUeeU6EEEFb2wIctj2SJfvfKJ/324PwaJjln2cvxH8NSQ1py7SvFmKYhH7RobgvFzB+S8+BAoKmkBmlDECAYS1Gawixo5+e4VxiH5gqwsVEKdaR6iJzjbr/Az9muyH/pc4DSMf4V3vRaW3E8xYEdPmDydxnrsqdpFdhPF12Tk5ruoKI05ymr479tgcRxCHMt2uBO9OW+OjkMQtebU=");

    private SikkerDigitalPostKlient postklient;

    @Before
    public void setUp() {
        KlientKonfigurasjon klientKonfigurasjon = KlientKonfigurasjon.builder()
                .meldingsformidlerRoot("https://qaoffentlig.meldingsformidler.digipost.no/api/")
                        //.meldingsformidlerRoot("http://localhost:8049")
                .build();

        Avsender avsender = ObjectMother.avsender();

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

        postklient.hentKvittering(kvitteringForespoersel);
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
