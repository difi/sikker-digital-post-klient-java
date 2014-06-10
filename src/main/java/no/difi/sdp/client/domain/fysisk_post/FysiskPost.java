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

import no.difi.sdp.client.domain.Sertifikat;

public class FysiskPost {

    private String orgNummerPrintleverandoer;
    private Sertifikat printleverandoerSertifikat;

    private UtenlandskPostadresse utenlandskAdresse;
    private NorskPostadresse norskAdresse;
    private NorskPostadresse returadresse;

    private PostType postType;

    private FysiskPost(String orgNummerPrintleverandoer, Sertifikat printleverandoerSertifikat, NorskPostadresse norskPostadresse, UtenlandskPostadresse utenlandskPostadresse, NorskPostadresse returadresse) {
        if ((norskPostadresse != null && utenlandskPostadresse != null) || (norskPostadresse == null && utenlandskPostadresse == null)) {
            throw new IllegalArgumentException("Must set either norsk postadresse or utenlandsk postadresse");
        }

        this.utenlandskAdresse = utenlandskPostadresse;
        this.norskAdresse = norskPostadresse;
        this.orgNummerPrintleverandoer = orgNummerPrintleverandoer;
        this.printleverandoerSertifikat = printleverandoerSertifikat;
        this.returadresse = returadresse;
    }

    public static Builder builder(String orgNummerPrintleverandoer, Sertifikat printleverandoerSertifikat, NorskPostadresse norskAdresse, NorskPostadresse returadresse) {
        return new Builder(orgNummerPrintleverandoer, printleverandoerSertifikat, norskAdresse, null, returadresse);
    }

    public static Builder builder(String orgNummerPrintleverandoer, Sertifikat printleverandoerSertifikat, UtenlandskPostadresse utenlandskAdresse, NorskPostadresse returadresse) {
        return new Builder(orgNummerPrintleverandoer, printleverandoerSertifikat, null, utenlandskAdresse, returadresse);
    }

    public static class Builder {

        private final FysiskPost target;
        private boolean built = false;

        private Builder(String orgNummerPrintleverandoer, Sertifikat printleverandoerSertifikat, NorskPostadresse norskAdresse, UtenlandskPostadresse utenlandskAdresse, NorskPostadresse returadresse) {
            target = new FysiskPost(orgNummerPrintleverandoer, printleverandoerSertifikat, norskAdresse, utenlandskAdresse, returadresse);
        }

        public Builder postType(PostType postType) {
            target.postType = postType;
            return this;
        }

        public FysiskPost build() {
            if (built) throw new IllegalStateException("Can't build twice");
            built = true;

            return target;
        }
    }
}
