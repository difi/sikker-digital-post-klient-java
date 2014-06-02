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
package no.difi.sdp.client.domain.kvittering;

import java.util.Date;

public class TilbaketrekkingsKvittering extends ForretningsKvittering {

    private String beskrivelse;
    private TilbaketrekkingsStatus status;

    private TilbaketrekkingsKvittering(Date tidspunkt, String konversasjonsId, String messageId, String refToMessageId, TilbaketrekkingsStatus status) {
        super(tidspunkt, konversasjonsId, messageId, refToMessageId);
        this.status = status;
    }

    public String getBeskrivelse() {
        return beskrivelse;
    }

    public TilbaketrekkingsStatus getStatus() {
        return status;
    }

    public static Builder builder(Date tidspunkt, String konverasjonsId, String messageId, String refToMessageId, TilbaketrekkingsStatus status) {
        return new Builder(tidspunkt, konverasjonsId, messageId, refToMessageId, status);
    }

    public static class Builder {
        private TilbaketrekkingsKvittering target;
        private boolean built = false;

        public Builder(Date tidspunkt, String konversasjonsId, String messageId, String refToMessageId, TilbaketrekkingsStatus status) {
            target = new TilbaketrekkingsKvittering(tidspunkt, konversasjonsId, messageId, refToMessageId, status);
        }

        public Builder status(TilbaketrekkingsStatus status) {
            target.status = status;
            return this;
        }

        public Builder beskrivelse(String beskrivelse) {
            target.beskrivelse = beskrivelse;
            return this;
        }

        public TilbaketrekkingsKvittering build() {
            if (built) throw new IllegalStateException("Can't build twice");
            built = true;
            return target;
        }
    }
}
