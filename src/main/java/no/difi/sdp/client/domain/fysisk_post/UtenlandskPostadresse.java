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
package no.difi.sdp.client.domain.fysisk_post;

public class UtenlandskPostadresse {

    private String navn;
    private String adresselinje1;
    private String adresselinje2;
    private String adresselinje3;
    private String adresselinje4;
    private String land;
    private String landkode;

    private UtenlandskPostadresse(String navn, String adresselinje1, String adresselinje2, String adresselinje3, String adresselinje4, String land, String landkode) {
        this.navn = navn;
        this.adresselinje1 = adresselinje1;
        this.adresselinje2 = adresselinje2;
        this.adresselinje3 = adresselinje3;
        this.adresselinje4 = adresselinje4;
        this.land = land;
        this.landkode = landkode;
    }

    public static Builder builder(String navn, String adresselinje1, String adresselinje2, String adresselinje3, String adresselinje4, String land, String landkode) {
        return new Builder(navn, adresselinje1, adresselinje2, adresselinje3, adresselinje4, land, landkode);
    }

    public static class Builder {

        private final UtenlandskPostadresse target;
        private boolean built = false;

        private Builder(String navn, String adresselinje1, String adresselinje2, String adresselinje3, String adresselinje4, String land, String landkode) {
            target = new UtenlandskPostadresse(navn, adresselinje1, adresselinje2, adresselinje3, adresselinje4, land, landkode);
        }

        public UtenlandskPostadresse build() {
            if (built) throw new IllegalStateException("Can't build twice");
            built = true;

            return target;
        }
    }
}
