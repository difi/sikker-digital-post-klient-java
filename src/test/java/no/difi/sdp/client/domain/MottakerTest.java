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

import no.difi.sdp.client.ObjectMother;
import org.junit.Test;

public class MottakerTest {

    private Sertifikat mottakerSertifikat = ObjectMother.mottakerSertifikat();

    @Test(expected = IllegalArgumentException.class)
    public void test_kan_ikke_lage_mottaker_uten_personidentifikator() {
        Mottaker.builder(null, "postkasseadresse", mottakerSertifikat, "984661185").build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_kan_ikke_lage_mottaker_uten_postkasseadresse() {
        Mottaker.builder("01129955131", "", mottakerSertifikat, "984661185").build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_kan_ikke_lage_mottaker_uten_sertifikat() {
        Mottaker.builder("01129955131", "postkasseadresse", null, "984661185").build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_kan_ikke_lage_mottaker_uten_organisasjonsNummerPostkasse() {
        Mottaker.builder("01129955131", "postkasseadresse", mottakerSertifikat, "").build();
    }

}
