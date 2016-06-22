package no.difi.sdp.client2.domain.kvittering;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.Instant;

import static org.fest.assertions.api.Assertions.assertThat;


public class KvitteringsInfoTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void builder_initializes() throws Exception {
        String konversasjonsid = "konversasjonsid";
        String referanse = "referanse";
        Instant tidspunkt = Instant.now();

        KvitteringsInfo kvitteringsInfo = KvitteringsInfo.builder()
                .konversasjonsId(konversasjonsid)
                .referanseTilMeldingId(referanse)
                .tidspunkt(tidspunkt).build();

        assertThat(kvitteringsInfo.getKonversasjonsId()).isEqualTo(konversasjonsid);
        assertThat(kvitteringsInfo.getReferanseTilMeldingId()).isEqualTo(referanse);
        assertThat(kvitteringsInfo.getTidspunkt()).isEqualTo(tidspunkt);
    }


    @Test
    public void builder_fails_on_konversasjonsid_not_initialized() throws Exception {
        String referanse = "referanse";
        Instant tidspunkt = Instant.now();

        thrown.expect(RuntimeException.class);
        KvitteringsInfo kvitteringsInfo = KvitteringsInfo.builder()
                .referanseTilMeldingId(referanse)
                .tidspunkt(tidspunkt).build();
    }

    @Test
    public void builder_fails_on_referanse_not_initialized() throws Exception {
        String konversasjonsid = "konversasjonsid";
        Instant tidspunkt = Instant.now();
        thrown.expect(RuntimeException.class);

        KvitteringsInfo kvitteringsInfo = KvitteringsInfo.builder()
                .konversasjonsId(konversasjonsid)
                .tidspunkt(tidspunkt).build();
    }

    @Test
    public void builder_fails_on_tidspunkt_not_initialized() throws Exception {
        String konversasjonsid = "konversasjonsid";
        String referanse = "referanse";
        thrown.expect(RuntimeException.class);

        KvitteringsInfo kvitteringsInfo = KvitteringsInfo.builder()
                .konversasjonsId(konversasjonsid)
                .referanseTilMeldingId(referanse)
                .build();
    }
}