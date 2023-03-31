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
package no.difi.sdp.client2.domain.digital_post;

import java.util.ArrayList;
import java.util.List;

public class SmsVarsel extends Varsel {

    private String mobilnummer;

    private SmsVarsel(String mobilnummer, String varslingsTekst) {
        super(varslingsTekst);
        this.mobilnummer = mobilnummer;
    }

    /**
     * @param mobilnummer Mobiltelefonnummer varselet skal sendes til.
     * @param varslingsTekst Avsenderstyrt varslingstekst som skal inngå i varselet.
     */
    public static Builder builder(String mobilnummer, String varslingsTekst) {
        return new Builder(mobilnummer, varslingsTekst);
    }

    public String getMobilnummer() {
        return mobilnummer;
    }

    public static class Builder {
        private SmsVarsel target;
        private boolean built = false;

        private Builder(String mobilnummer, String varslingsTekst) {
            target = new SmsVarsel(mobilnummer, varslingsTekst);
        }

        /**
         * Antall dager etter brevet er tilgjengeliggjort for mottaker det første, andre osv varsel skal sendes.
         *
         * Eksempel: 0, 2, 5, 10
         * Hvis brevet blir tilgjengeliggjort 1.7.2014 vil det bli sendt varsel:
         * <ul>
         *     <li>1.7.2014</li>
         *     <li>3.7.2014</li>
         *     <li>6.7.2014</li>
         *     <li>11.7.2014</li>
         * </ul>
         *
         * Det vil ikke bli sendt flere varsler etter mottakeren har åpnet brevet.
         *
         * Standard er ett varsel samtidig som brevet blir tilgjengeliggjort for mottaker.
         */
        public Builder varselEtterDager(List<Integer> varselEtterDager) {
            target.dagerEtter = new ArrayList<Integer>(varselEtterDager);
            return this;
        }

        public SmsVarsel build() {
            if (built) throw new IllegalStateException("Can't build twice");
            built = true;
            return target;
        }
    }
}
