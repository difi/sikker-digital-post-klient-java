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

import java.io.InputStream;

public class Dokument {

    /**
     * Tittel som vises til brukeren gitt riktig sikkerhetsnivå.
     */
    private String tittel;

    /**
     * MIME-type for dokumentet. For liste over tillatte MIME-typer, se <a href="http://begrep.difi.no">http://begrep.difi.no</a> }
     */
    private String mimeType;

    /**
     * Filnavnet til dokumentet.
     */
    private String href;
    /**
     * Dokumentet som en strøm
     */
    private InputStream dokument;

}
