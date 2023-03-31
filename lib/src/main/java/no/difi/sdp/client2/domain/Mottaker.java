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

public class Mottaker {

    private final String personidentifikator;
    private final String postkasseadresse;
    private final TekniskMottaker postkasse;

    private Mottaker(String personidentifikator, String postkasseadresse, Sertifikat mottakerSertifikat, Organisasjonsnummer organisasjonsnummerPostkasse) {
        this.personidentifikator = personidentifikator;
        this.postkasseadresse = postkasseadresse;
        this.postkasse = new TekniskMottaker(organisasjonsnummerPostkasse, mottakerSertifikat);
    }

    public TekniskMottaker getMottakersPostkasse() {
    	return postkasse;
    }

    public String getPostkasseadresse() {
        return postkasseadresse;
    }

    public String getPersonidentifikator() {
        return personidentifikator;
    }


    /**
     * Informasjon om mottaker. Vil vanligvis være hentet fra <a href="http://begrep.difi.no/Oppslagstjenesten/">Oppslagstjenesten</a>.
     *
     * @param personidentifikator Identifikator (fødselsnummer eller D-nummer) til mottaker av brevet.
     * @param postkasseadresse Mottakerens adresse hos postkasseleverandøren.
     * @param mottakerSertifikat Mottakers sertifikat.
     * @param organisasjonsnummerPostkasse Identifikator (organisasjonsnummer) til virksomheten som er sluttmottaker i meldingsprosessen.
     */
    public static Builder builder(String personidentifikator, String postkasseadresse, Sertifikat mottakerSertifikat, Organisasjonsnummer organisasjonsnummerPostkasse) {
        return new Builder(personidentifikator, postkasseadresse, mottakerSertifikat, organisasjonsnummerPostkasse);
    }

    public static class Builder {
        private final Mottaker target;
        private boolean built = false;

        private Builder(String personidentifikator, String postkasseadresse, Sertifikat mottakerSertifikat, Organisasjonsnummer organisasjonsnummerPostkasse) {
            target = new Mottaker(personidentifikator, postkasseadresse, mottakerSertifikat, organisasjonsnummerPostkasse);
        }

        public Mottaker build() {
            if (built) throw new IllegalStateException("Can't build twice");
            built = true;
            return target;
        }
    }
}
