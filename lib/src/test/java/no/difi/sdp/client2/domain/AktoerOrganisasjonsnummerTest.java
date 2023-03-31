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
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class AktoerOrganisasjonsnummerTest {
    @Test
    public void create_organisasjonsnummer_from_string() {
        String orgnr = "984661185";

        AktoerOrganisasjonsnummer organisasjonsnummer = AktoerOrganisasjonsnummer.of(orgnr);

        assertThat(organisasjonsnummer.getOrganisasjonsnummer(), equalTo(orgnr));
    }

    @Test
    public void create_organisasjonsnummer_from_class() {
        Organisasjonsnummer orgnr = Organisasjonsnummer.of("984661185");

        AktoerOrganisasjonsnummer organisasjonsnummer = AktoerOrganisasjonsnummer.of(orgnr);

        assertThat(organisasjonsnummer.getOrganisasjonsnummer(), equalTo(orgnr.getOrganisasjonsnummer()));
    }

    @Test
    public void forfrem_til_avsender() {
        AktoerOrganisasjonsnummer organisasjonsnummer = AktoerOrganisasjonsnummer.of("984661185");

        AvsenderOrganisasjonsnummer avsenderOrganisasjonsnummer = organisasjonsnummer.forfremTilAvsender();

        assertThat(avsenderOrganisasjonsnummer.getOrganisasjonsnummerMedLandkode(), equalTo(organisasjonsnummer.getOrganisasjonsnummerMedLandkode()));
    }

    @Test
    public void forfrem_til_databehandler() {
        AktoerOrganisasjonsnummer organisasjonsnummer = AktoerOrganisasjonsnummer.of("984661185");

        DatabehandlerOrganisasjonsnummer databehandlerOrganisasjonsnummer = organisasjonsnummer.forfremTilDatabehandler();

        assertThat(databehandlerOrganisasjonsnummer.getOrganisasjonsnummerMedLandkode(), equalTo(organisasjonsnummer.getOrganisasjonsnummerMedLandkode()));
    }
}