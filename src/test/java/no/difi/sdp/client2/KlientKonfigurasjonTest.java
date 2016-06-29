package no.difi.sdp.client2;

import org.junit.Test;

import java.net.URI;

import static org.fest.assertions.api.Assertions.assertThat;

public class KlientKonfigurasjonTest {

    @Test
    public void default_builder_initializes_meldingsformidler_root() {
        URI meldingsformidlerRoot = URI.create("http://meldingsformidlerroot.no");

        KlientKonfigurasjon klientKonfigurasjon = KlientKonfigurasjon
                .builder(meldingsformidlerRoot)
                .build();

        assertThat(klientKonfigurasjon.getMeldingsformidlerRoot()).isEqualTo(meldingsformidlerRoot);


    }
}