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
package no.difi.sdp.client2.domain;

/**
 * Avsender som beskrevet i <a href="http://begrep.difi.no/SikkerDigitalPost/forretningslag/Aktorer">oversikten over aktører</a>.
 */
public class Avsender {

    private final AvsenderOrganisasjonsnummer organisasjonsnummer;
    private String avsenderIdentifikator;
    private String fakturaReferanse;

    public Avsender(AvsenderOrganisasjonsnummer organisasjonsnummer) {
        this.organisasjonsnummer = organisasjonsnummer;
    }

    public static Builder builder(AvsenderOrganisasjonsnummer organisasjonsnummer) {
        return new Builder(organisasjonsnummer);
    }

    public String getAvsenderIdentifikator() {
        return avsenderIdentifikator;
    }

    public String getFakturaReferanse() {
        return fakturaReferanse;
    }

    public AvsenderOrganisasjonsnummer getOrganisasjonsnummer() {
        return organisasjonsnummer;
    }

    public static class Builder {

        private final Avsender target;
        private boolean built = false;

        private Builder(AvsenderOrganisasjonsnummer organisasjonsnummer) {
            target = new Avsender(organisasjonsnummer);
        }

        public Builder fakturaReferanse(String fakturaReferanse) {
            target.fakturaReferanse = fakturaReferanse;
            return this;
        }

        /**
         * Brukes for å identifisere en ansvarlig enhet innen for en virksomhet. Benyttes dersom det er behov for å skille mellom ulike enheter hos avsender.
         *
         * @param avsenderIdentifikator Identifikator som er tildelt av Sentralforvalter ved tilkobling til tjenesten.
         */
        public Builder avsenderIdentifikator(String avsenderIdentifikator) {
            target.avsenderIdentifikator = avsenderIdentifikator;
            return this;
        }

        public Avsender build() {
            if (built) throw new IllegalStateException("Kan ikke bygges flere ganger.");
            built = true;
            return this.target;
        }
    }

}
