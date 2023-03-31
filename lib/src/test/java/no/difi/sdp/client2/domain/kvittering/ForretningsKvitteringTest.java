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

import no.digipost.api.representations.KanBekreftesSomBehandletKvittering;
import no.digipost.api.representations.KvitteringsReferanse;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class ForretningsKvitteringTest {

    @Test
    public void constructor_initializes_properly() {
        KanBekreftesSomBehandletKvittering kanBekreftesSomBehandletKvittering = new KanBekreftesSomBehandletKvittering() {
            @Override
            public String getMeldingsId() {
                return "MeldingsId";
            }

            @Override
            public KvitteringsReferanse getReferanseTilMeldingSomKvitteres() {
                return null;
            }
        };
        KvitteringsInfo kvitteringsInfo = new KvitteringsInfo("konversasjonsId", "referanseTilMeldingId", Instant.now());
        LeveringsKvittering leveringsKvittering = new LeveringsKvittering(kanBekreftesSomBehandletKvittering, kvitteringsInfo);

        assertThat(leveringsKvittering.getReferanseTilMeldingSomKvitteres(), equalTo(kanBekreftesSomBehandletKvittering.getReferanseTilMeldingSomKvitteres()));
        assertThat(leveringsKvittering.getMeldingsId(), equalTo(kanBekreftesSomBehandletKvittering.getMeldingsId()));
        assertThat(leveringsKvittering.getKonversasjonsId(), equalTo(kvitteringsInfo.getKonversasjonsId()));
        assertThat(leveringsKvittering.getReferanseTilMeldingId(), equalTo(kvitteringsInfo.getReferanseTilMeldingId()));
        assertThat(leveringsKvittering.getTidspunkt(), equalTo(kvitteringsInfo.getTidspunkt()));
    }
}