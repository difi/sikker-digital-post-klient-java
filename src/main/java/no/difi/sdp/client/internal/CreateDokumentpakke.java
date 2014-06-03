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
package no.difi.sdp.client.internal;

import no.difi.sdp.client.asice.ArchivedASiCE;
import no.difi.sdp.client.asice.CreateASiCE;
import no.difi.sdp.client.domain.Avsender;
import no.difi.sdp.client.domain.Forsendelse;
import no.difi.sdp.client.domain.Sertifikat;
import no.posten.dpost.offentlig.api.representations.Dokumentpakke;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateDokumentpakke {

    private final CreateASiCE createASiCE;
    private final CreateCMSDocument createCMS;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public CreateDokumentpakke() {
        createASiCE = new CreateASiCE();
        createCMS = new CreateCMSDocument();
    }

    public Dokumentpakke createDokumentpakke(Avsender avsender, Forsendelse forsendelse) {
        log.info("Creating dokumentpakke");
        ArchivedASiCE archivedASiCE = createASiCE.createAsice(avsender, forsendelse);

        Sertifikat mottakerSertifikat = forsendelse.getDigitalPost().getMottaker().getSertifikat();

        log.info("Creating CMS document");
        CMSDocument cms = createCMS.createCMS(archivedASiCE.getBytes(), mottakerSertifikat);
        return new Dokumentpakke(cms.getBytes());
    }

}
