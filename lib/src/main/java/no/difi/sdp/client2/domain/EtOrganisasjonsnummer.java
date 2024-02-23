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

import no.digipost.api.representations.Organisasjonsnummer;

public class EtOrganisasjonsnummer implements AvsenderOrganisasjonsnummer, DatabehandlerOrganisasjonsnummer {

    private Organisasjonsnummer organisasjonsnummer;

    EtOrganisasjonsnummer(String organisasjonsnummer) {
        this.organisasjonsnummer = Organisasjonsnummer.of(organisasjonsnummer);
    }

    @Override
    public String getOrganisasjonsnummer() {
        return organisasjonsnummer.getOrganisasjonsnummer();
    }

    @Override
    public String getOrganisasjonsnummerMedLandkode() {
        return organisasjonsnummer.getOrganisasjonsnummerMedLandkode();
    }

    @Override
    public AvsenderOrganisasjonsnummer forfremTilAvsender() {
        return this;
    }

    @Override
    public DatabehandlerOrganisasjonsnummer forfremTilDatabehandler() {
        return this;
    }

    @Override
    public String toString() {
        return organisasjonsnummer.toString();
    }
}
