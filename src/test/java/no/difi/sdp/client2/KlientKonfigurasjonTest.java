package no.difi.sdp.client2;

import no.difi.sdp.client2.domain.Avsender;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class KlientKonfigurasjonTest {

    @Test
    public void default_builder_initializes_meldingsformidler_root() {
        String meldingsformidlerRoot =  "http://meldingsformidlerroot.no";
        Avsender avsender = Avsender.builder("orgnummer").build();

        KlientKonfigurasjon klientKonfigurasjon = KlientKonfigurasjon
                .builder(meldingsformidlerRoot)
                .build();

        assertThat(klientKonfigurasjon.getMeldingsformidlerRoot().toString()).isEqualTo(meldingsformidlerRoot);
    }
}