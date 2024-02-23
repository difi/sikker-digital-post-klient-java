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

import java.time.Instant;

public abstract class ForretningsKvittering implements KanBekreftesSomBehandletKvittering {

    private final KvitteringsInfo kvitteringsInfo;
    private final KanBekreftesSomBehandletKvittering kanBekreftesSomBehandletKvittering;

    public ForretningsKvittering(KanBekreftesSomBehandletKvittering kanBekreftesSomBehandletKvittering,  KvitteringsInfo kvitteringsInfo){
        this.kanBekreftesSomBehandletKvittering = kanBekreftesSomBehandletKvittering;
        this.kvitteringsInfo = kvitteringsInfo;
    }

    /**
     * Gir hvilken subtype av ForretningsKvittering og konversasjonsId som String.
     * Subklasser kan override dette.
     */
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "konversasjonsId=" + kvitteringsInfo.getKonversasjonsId() +
                "}";
    }

    public Instant getTidspunkt() {
        return kvitteringsInfo.getTidspunkt();
    }

    public String getKonversasjonsId() {
        return kvitteringsInfo.getKonversasjonsId();
    }

    public String getReferanseTilMeldingId() {
        return kvitteringsInfo.getReferanseTilMeldingId();
    }

    @Override
    public String getMeldingsId(){
       return kanBekreftesSomBehandletKvittering.getMeldingsId();
    }

    @Override
    public KvitteringsReferanse getReferanseTilMeldingSomKvitteres(){
        return kanBekreftesSomBehandletKvittering.getReferanseTilMeldingSomKvitteres();
    }

}
