package no.difi.sdp.client2;

import no.difi.sdp.client2.domain.Miljo;
import org.junit.Test;

import java.net.URI;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class KlientKonfigurasjonTest {

    @Test
    public void uri_builder_initializes_meldingsformidler_root_and_miljo() {
        URI meldingsformidlerRoot = URI.create("http://meldingsformidlerroot.no");

        KlientKonfigurasjon klientKonfigurasjon = KlientKonfigurasjon
                .builder(meldingsformidlerRoot)
                .build();

        assertThat(klientKonfigurasjon.getMeldingsformidlerRoot().getBaseUri(), equalTo(meldingsformidlerRoot));
        assertThat(klientKonfigurasjon.getMiljo().getMeldingsformidlerRoot(), equalTo(meldingsformidlerRoot));
    }

    @Test
    public void miljo_builder_initializes_meldingsformidler_root_and_miljo() {
        Miljo funksjoneltTestmiljo = Miljo.FUNKSJONELT_TESTMILJO;
        KlientKonfigurasjon klientKonfigurasjon = KlientKonfigurasjon
                .builder(funksjoneltTestmiljo)
                .build();

        Miljo actualMiljo = klientKonfigurasjon.getMiljo();

        assertThat(actualMiljo, equalTo(funksjoneltTestmiljo));
        assertEquals(klientKonfigurasjon.getMeldingsformidlerRoot().getBaseUri(), actualMiljo.getMeldingsformidlerRoot());
    }

}