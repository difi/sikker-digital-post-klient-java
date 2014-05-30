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

public class BekreftelsesKvittering {

    private String konversasjonsId;
    private String messsageId;

    private BekreftelsesKvittering(String konversasjonsId, String messsageId) {
        this.konversasjonsId = konversasjonsId;
        this.messsageId = messsageId;
    }

    public static Builder builder(String konversasjonsId, String messageId) {
        return new Builder(konversasjonsId, messageId);
    }

    public static class Builder {
        private BekreftelsesKvittering target;
        private boolean built = false;

        public Builder(String konversasjonsId, String messageId) {
            target = new BekreftelsesKvittering(konversasjonsId, messageId);
        }

        public BekreftelsesKvittering build() {
            if (built) throw new IllegalStateException("Can't build twice");
            built = true;
            return target;
        }
    }
}
