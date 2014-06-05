/**
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
package no.difi.sdp.client.domain;

import no.difi.sdp.client.domain.digital_post.DigitalPost;
import org.junit.Test;

import static no.difi.sdp.client.ObjectMother.mottaker;

public class DigitalPostTest {

    @Test(expected = IllegalArgumentException.class)
    public void test_kan_ikke_lage_digitalpost_uten_mottaker() {
        DigitalPost.builder(null, "Denne tittelen er ikke sensitiv").build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_kan_ikke_lage_digitalpost_uten_ikkeSensitivTittel() {
        DigitalPost.builder(mottaker(), "").build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_kan_ikke_nullstille_sikkerhetsnivaa() {
        DigitalPost.builder(mottaker(), "Denne tittelen er ikke sensitiv")
                .sikkerhetsnivaa(null)
                .build();
    }

}
