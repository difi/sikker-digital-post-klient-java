package no.difi.sdp.client2;

import no.difi.sdp.client2.domain.Behandlingsansvarlig;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class KlientKonfigurasjonTest {

    @Test
    public void DefaultBuilder_InitializesMeldingsformidlerRoot() {
        String meldingsformidlerRoot =  "http://meldingsformidlerroot.no";
        Behandlingsansvarlig behandlingsansvarlig = Behandlingsansvarlig.builder("orgnummer").build();

        KlientKonfigurasjon klientKonfigurasjon = KlientKonfigurasjon
                .builder(meldingsformidlerRoot)
                .build();

        assertThat(klientKonfigurasjon.getMeldingsformidlerRoot().toString()).isEqualTo(meldingsformidlerRoot);
    }
}