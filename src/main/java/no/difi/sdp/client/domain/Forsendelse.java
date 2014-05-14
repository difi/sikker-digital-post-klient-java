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
package no.difi.sdp.client.domain;

import java.util.Date;

public class Forsendelse {

    private String konversasjonsId;
    private Prioritet prioritet;
    private Mottaker mottaker;

    private Dokumentpakke dokumentpakke;

    /**
     * Når brevet tilgjengeliggjøres for mottaker. Standard er nå.
     */
    private Date virkningsdato = new Date();

    private boolean aapningskvittering;

    /**
     * Sikkerhetsnivå som kreves for å åpne brevet. Standard er nivå 3 (passord).
     */
    private Sikkerhetsnivaa sikkerhetsnivaa = Sikkerhetsnivaa.NIVAA_3;

    /**
     * Ikke-sensitiv tittel på brevet.
     */
    private String tittel;

    /**
     * Varsler som skal sendes til mottaker av brevet.
     */
    private Varsler varsler;

}
