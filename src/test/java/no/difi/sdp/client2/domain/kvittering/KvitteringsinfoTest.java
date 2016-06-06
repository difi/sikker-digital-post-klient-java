package no.difi.sdp.client2.domain.kvittering;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.Instant;

import static org.fest.assertions.api.Assertions.assertThat;


public class KvitteringsinfoTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void testBuilder_initializes() throws Exception {
        String konversasjonsid = "konversasjonsid";
        String referanse = "referanse";
        Instant tidspunkt = Instant.now();

        Kvitteringsinfo kvitteringsinfo = Kvitteringsinfo.builder()
                .konversasjonsId(konversasjonsid)
                .referanseTilMeldingId(referanse)
                .tidspunkt(tidspunkt).build();

        assertThat(kvitteringsinfo.getKonversasjonsId()).isEqualTo(konversasjonsid);
        assertThat(kvitteringsinfo.getReferanseTilMeldingId()).isEqualTo(referanse);
        assertThat(kvitteringsinfo.getTidspunkt()).isEqualTo(tidspunkt);
    }


    @Test
    public void testBuilder_failsOnKonversasjonsidNotInitialized() throws Exception {
        String referanse = "referanse";
        Instant tidspunkt = Instant.now();

        thrown.expect(RuntimeException.class);
        Kvitteringsinfo kvitteringsinfo = Kvitteringsinfo.builder()
                .referanseTilMeldingId(referanse)
                .tidspunkt(tidspunkt).build();
    }

    @Test
    public void testBuilder_failsOnReferanseNotInitialized() throws Exception {
        String konversasjonsid = "konversasjonsid";
        Instant tidspunkt = Instant.now();
        thrown.expect(RuntimeException.class);

        Kvitteringsinfo kvitteringsinfo = Kvitteringsinfo.builder()
                .konversasjonsId(konversasjonsid)
                .tidspunkt(tidspunkt).build();
    }

    @Test
    public void testBuilder_failsOnTidspunktNotInitialized() throws Exception {
        String konversasjonsid = "konversasjonsid";
        String referanse = "referanse";
        thrown.expect(RuntimeException.class);

        Kvitteringsinfo kvitteringsinfo = Kvitteringsinfo.builder()
                .konversasjonsId(konversasjonsid)
                .referanseTilMeldingId(referanse)
                .build();
    }
}