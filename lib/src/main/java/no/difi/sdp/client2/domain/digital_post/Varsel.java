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

import java.util.List;

import static java.util.Arrays.asList;

public abstract class Varsel {

    protected Varsel(String varslingsTekst) {
        this.varslingsTekst = varslingsTekst;
    }

    protected String varslingsTekst;
    protected List<Integer> dagerEtter = asList(0);

    public String getVarslingsTekst() {
        return varslingsTekst;
    }

    public List<Integer> getDagerEtter() {
        return dagerEtter;
    }
}
