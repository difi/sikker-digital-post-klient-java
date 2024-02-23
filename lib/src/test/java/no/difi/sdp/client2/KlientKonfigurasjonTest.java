/*
 * Copyright (C) Posten Norge AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package no.difi.sdp.client2;

import no.difi.sdp.client2.domain.Miljo;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class KlientKonfigurasjonTest {

    @Test
    public void uri_builder_initializes_meldingsformidler_root_and_miljo() {
        URI meldingsformidlerRoot = URI.create("http://meldingsformidlerroot.no");

        @SuppressWarnings("deprecation")
        KlientKonfigurasjon klientKonfigurasjon = KlientKonfigurasjon.builder(meldingsformidlerRoot).build();

        assertThat(klientKonfigurasjon.getMeldingsformidlerRoot().getBaseUri(), is(meldingsformidlerRoot));
        assertThat(klientKonfigurasjon.getMiljo().getMeldingsformidlerRoot(), is(meldingsformidlerRoot));
    }

    @Test
    public void miljo_builder_initializes_meldingsformidler_root_and_miljo() {
        Miljo funksjoneltTestmiljo = Miljo.FUNKSJONELT_TESTMILJO;
        KlientKonfigurasjon klientKonfigurasjon = KlientKonfigurasjon
                .builder(funksjoneltTestmiljo)
                .build();

        Miljo actualMiljo = klientKonfigurasjon.getMiljo();

        assertThat(actualMiljo, is(funksjoneltTestmiljo));
        assertThat(actualMiljo.getMeldingsformidlerRoot(), is(klientKonfigurasjon.getMeldingsformidlerRoot().getBaseUri()));
    }

}
