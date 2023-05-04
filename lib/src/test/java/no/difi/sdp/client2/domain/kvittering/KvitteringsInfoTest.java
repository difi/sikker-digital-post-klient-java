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
package no.difi.sdp.client2.domain.kvittering;

import no.difi.sdp.client2.domain.kvittering.KvitteringsInfo.Builder;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class KvitteringsInfoTest {

    @Test
    public void builder_initializes() throws Exception {
        String konversasjonsid = "konversasjonsid";
        String referanse = "referanse";
        Instant tidspunkt = Instant.now();

        KvitteringsInfo kvitteringsInfo = KvitteringsInfo.builder()
                .konversasjonsId(konversasjonsid)
                .referanseTilMeldingId(referanse)
                .tidspunkt(tidspunkt).build();

        assertThat(kvitteringsInfo.getKonversasjonsId(), equalTo(konversasjonsid));
        assertThat(kvitteringsInfo.getReferanseTilMeldingId(), equalTo(referanse));
        assertThat(kvitteringsInfo.getTidspunkt(), equalTo(tidspunkt));
    }


    @Test
    public void builder_fails_on_konversasjonsid_not_initialized() throws Exception {
        String referanse = "referanse";
        Instant tidspunkt = Instant.now();

        Builder kvitteringsInfoBuilder = KvitteringsInfo.builder()
                .referanseTilMeldingId(referanse)
                .tidspunkt(tidspunkt);

        assertThrows(RuntimeException.class, () -> kvitteringsInfoBuilder.build());
    }

    @Test
    public void builder_fails_on_referanse_not_initialized() throws Exception {
        String konversasjonsid = "konversasjonsid";
        Instant tidspunkt = Instant.now();

        Builder kvitteringsInfoBuilder = KvitteringsInfo.builder()
                .konversasjonsId(konversasjonsid)
                .tidspunkt(tidspunkt);

        assertThrows(RuntimeException.class, () -> kvitteringsInfoBuilder.build());
    }

    @Test
    public void builder_fails_on_tidspunkt_not_initialized() throws Exception {
        String konversasjonsid = "konversasjonsid";
        String referanse = "referanse";

        Builder kvitteringsInfoBuilder = KvitteringsInfo.builder()
                .konversasjonsId(konversasjonsid)
                .referanseTilMeldingId(referanse);

        assertThrows(RuntimeException.class, () -> kvitteringsInfoBuilder.build());
    }
}