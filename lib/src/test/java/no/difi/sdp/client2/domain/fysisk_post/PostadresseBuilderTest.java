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
package no.difi.sdp.client2.domain.fysisk_post;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static no.difi.sdp.client2.domain.fysisk_post.KonvoluttAdresse.Type.NORSK;
import static no.difi.sdp.client2.domain.fysisk_post.KonvoluttAdresse.Type.UTENLANDSK;
import static no.difi.sdp.client2.domain.fysisk_post.Landkoder.Predefinert.USA;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PostadresseBuilderTest {

    @Test
    public void inkluderer_kun_ikke_null_adresselinjer() {
        KonvoluttAdresse adresse = KonvoluttAdresse.build("Ola Hansen").iNorge("Osloveien 5", null, null, "0560", "Oslo").build();
        assertThat(adresse.getAdresselinjer(), equalTo(Arrays.asList("Osloveien 5")));

        adresse = KonvoluttAdresse.build("Ola Hansen").iUtlandet("Somewhere St. 5", null, "70482 City", null, USA).build();
        assertEquals(adresse.getAdresselinjer(), Arrays.asList("Somewhere St. 5", "70482 City"));
    }

    @Test
    public void norsk_adresse_er_norsk() {
        KonvoluttAdresse adresse = KonvoluttAdresse.build("Ola Hansen").iNorge("Osloveien 5", null, null, "0560", "Oslo").build();
        assertThat(adresse.getType(), equalTo(NORSK));
    }

    @Test
    public void utenlandsk_adresse_er_utenlandsk() {
        KonvoluttAdresse adresse = KonvoluttAdresse.build("Ola Hansen").iUtlandet("Somewhere St. 5", "10592 New York", null, null, USA).build();
        assertThat(adresse.getType(), equalTo(UTENLANDSK));
        assertThat(adresse.getLand(), is(nullValue()));
        assertThat(adresse.getLandkode(), equalTo(USA.getKode()));
    }

    @Test
    public void utenlandsk_adresse_med_landnavn_kan_hente_navn() {
        KonvoluttAdresse adresse = KonvoluttAdresse.build("Ola Hansen").iUtlandet("Somewhere St. 5", "10592 New York", null, null, "Sverige").build();
        assertThat(adresse.getType(), equalTo(UTENLANDSK));
        assertThat(adresse.getLand(), equalTo("Sverige"));
        assertThat(adresse.getLandkode(), is(nullValue()));
    }

}
