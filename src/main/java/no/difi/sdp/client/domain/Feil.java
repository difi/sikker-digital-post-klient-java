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

import no.difi.sdp.client.domain.kvittering.ForretningsKvittering;

import java.util.Date;

public class Feil extends ForretningsKvittering {

    private Feiltype feiltype;
    private String detaljer;

    private Feil(Date tidspunkt, String konversasjonsId, String refToMessageId, Feiltype feiltype) {
        super(tidspunkt, konversasjonsId, refToMessageId);
        this.feiltype = feiltype;
    }

    public Feiltype getFeiltype() {
        return feiltype;
    }

    public String getDetaljer() {
        return detaljer;
    }

    public static Builder builder(Date tidspunkt, String konversasjonsId, String refToMessageId, Feiltype feiltype) {
        return new Builder(tidspunkt, konversasjonsId, refToMessageId, feiltype);
    }

    public static class Builder {
        private Feil target;
        private boolean built = false;

        public Builder(Date tidspunkt, String konversasjonsId, String refToMessageId, Feiltype feiltype) {
            target = new Feil(tidspunkt, konversasjonsId, refToMessageId, feiltype);
        }

        public Builder detaljer(String detaljer) {
            target.detaljer = detaljer;
            return this;
        }

        public Feil build() {
            if (built) throw new IllegalStateException("Can't build twice");
            built = true;
            return target;
        }

    }
}
