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
package no.difi.sdp.client;

import java.net.URI;

public class KlientKonfigurasjon {

    private URI meldingsformidlerRoot = URI.create("https://meldingsformidler.digipost.no/api/ebms");

    private KlientKonfigurasjon() {}

    public URI getMeldingsformidlerRoot() {
        return meldingsformidlerRoot;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final KlientKonfigurasjon target;

        private Builder() {
            target = new KlientKonfigurasjon();
        }

        public Builder meldingsformidlerRoot(String meldingsformidlerRoot) {
            target.meldingsformidlerRoot = URI.create(meldingsformidlerRoot);
            return this;
        }

        public KlientKonfigurasjon build() {
            return target;
        }
    }
}
