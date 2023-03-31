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

import no.difi.sdp.client2.domain.Prioritet;

public class KvitteringForespoersel {

    private Prioritet prioritet;
    private String mpcId;

    private KvitteringForespoersel(Prioritet prioritet) {
        this.prioritet = prioritet;
    }

    public Prioritet getPrioritet() {
        return prioritet;
    }

    /**
     * @param prioritet Hvilken prioritet det forespørres kvittering for. De ulike prioritene kan ses på som egne køer for kvitteringer.
     *                  Dersom en forsendelse er sendt med normal prioritet, vil den kun dukke opp dersom det spørres om kvittering på normal prioritet.
     */
    public static Builder builder(Prioritet prioritet) {
        return new Builder(prioritet);
    }

    public String getMpcId() {
        return mpcId;
    }

    public static class Builder {
        private final KvitteringForespoersel target;
        private boolean built = false;

        private Builder(Prioritet prioritet) {
            target = new KvitteringForespoersel(prioritet);
        }

        /**
         * Brukes til å skille mellom ulike kvitteringskøer for samme tekniske avsender. En forsendelse gjort med en
         * MPC Id vil kun dukke opp i kvitteringskøen med samme MPC Id.
         *
         * Standardverdi er blank MPC Id.
         *
         * @see no.difi.sdp.client2.domain.Forsendelse.Builder#mpcId(String)
         */
        public Builder mpcId(String mpcId) {
            target.mpcId = mpcId;
            return this;
        }

        public KvitteringForespoersel build() {
            if (built) throw new IllegalStateException("Can't build twice");
            built = true;
            return target;
        }
    }
}
