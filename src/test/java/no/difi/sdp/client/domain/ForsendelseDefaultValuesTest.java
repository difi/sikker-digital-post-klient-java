package no.difi.sdp.client.domain;

import no.difi.sdp.client.domain.digital_post.DigitalPost;
import no.difi.sdp.client.domain.digital_post.Sikkerhetsnivaa;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import static org.fest.assertions.api.Assertions.assertThat;

public class ForsendelseDefaultValuesTest {

    private Forsendelse forsendelse;

    @Before
    public void setup() {
        Sertifikat gyldigSertfikat = Sertifikat.fraBase64String("MIIDFDCCAr6gAwIBAgIJALENVFrUMVgcMA0GCSqGSIb3DQEBBQUAMIGQMQswCQYDVQQGEwJOTzENMAsGA1UECBMET3NsbzENMAsGA1UEBxMET3NsbzENMAsGA1UEChMERElGSTENMAsGA1UECxMERElGSTEhMB8GA1UEAxMYU2lra2VyIERpZ2l0YWwgUG9zdCBUZXN0MSIwIAYJKoZIhvcNAQkBFhNkaWZpIGF0IGRpZmkgZG90IG5vMB4XDTE0MDUxNjA4MjQ0MloXDTE0MDYxNTA4MjQ0MlowgZAxCzAJBgNVBAYTAk5PMQ0wCwYDVQQIEwRPc2xvMQ0wCwYDVQQHEwRPc2xvMQ0wCwYDVQQKEwRESUZJMQ0wCwYDVQQLEwRESUZJMSEwHwYDVQQDExhTaWtrZXIgRGlnaXRhbCBQb3N0IFRlc3QxIjAgBgkqhkiG9w0BCQEWE2RpZmkgYXQgZGlmaSBkb3Qgbm8wXDANBgkqhkiG9w0BAQEFAANLADBIAkEA1OteZ0rH+269STIDm2ECmop593A+7v9ih6ydow11wCojGvNnHGjeollzTn+F7caRqLCl7vKr3uttINBFA7E34QIDAQABo4H4MIH1MB0GA1UdDgQWBBRkkkvwgXi/qqQHLyMDttBDCN8PNzCBxQYDVR0jBIG9MIG6gBRkkkvwgXi/qqQHLyMDttBDCN8PN6GBlqSBkzCBkDELMAkGA1UEBhMCTk8xDTALBgNVBAgTBE9zbG8xDTALBgNVBAcTBE9zbG8xDTALBgNVBAoTBERJRkkxDTALBgNVBAsTBERJRkkxITAfBgNVBAMTGFNpa2tlciBEaWdpdGFsIFBvc3QgVGVzdDEiMCAGCSqGSIb3DQEJARYTZGlmaSBhdCBkaWZpIGRvdCBub4IJALENVFrUMVgcMAwGA1UdEwQFMAMBAf8wDQYJKoZIhvcNAQEFBQADQQA43EV/uoAXPEyZSXg+9g/jkxrmhNHeG8evLSM3MqLeS6lO0P6hnGlhoF9GYjqMx7ntYdE8i8jt3a5GRupTIpHB");

        Mottaker mottaker = Mottaker.builder("01129955131", "postkasseadresse", gyldigSertfikat, "984661185")
                .build();

        DigitalPost digitalPost = DigitalPost.builder(mottaker, "Denne tittelen er ikke sensitiv")
                .build();

        Dokument hovedDokument = Dokument.builder("Sensitiv brevtittel", "faktura.pdf", new ByteArrayInputStream("hei".getBytes()))
                .mimeType("application/pdf")
                .build();

        Dokumentpakke dokumentpakke = Dokumentpakke.builder(hovedDokument)
                .vedlegg(new ArrayList<Dokument>())
                .build();

        forsendelse = Forsendelse.builder(digitalPost, dokumentpakke)
                .build();
    }

    @Test
    public void test_default_sikkerhetsnivaa_is_set_to_4() {
        assertThat(forsendelse.getDigitalPost().getSikkerhetsnivaa()).isEqualTo(Sikkerhetsnivaa.NIVAA_4);
    }

    @Test
    public void test_default_prioritet_is_set_to_NORMAL() {
        assertThat(forsendelse.getPrioritet()).isEqualTo(Prioritet.NORMAL);
    }

    @Test
    public void test_konversasjonsId_is_not_null_or_empty() {
        assertThat(forsendelse.getKonversasjonsId()).isNotEmpty();
    }
}
