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
package no.difi.sdp.client2.internal;

import no.difi.sdp.client2.asice.ArchivedASiCE;
import no.difi.sdp.client2.asice.CreateASiCE;
import no.difi.sdp.client2.domain.Databehandler;
import no.difi.sdp.client2.domain.Forsendelse;
import no.difi.sdp.client2.domain.Sertifikat;
import no.digipost.api.representations.Dokumentpakke;
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

    public Billable<Dokumentpakke> createDokumentpakke(Databehandler databehandler, Forsendelse forsendelse) {
        log.info("Creating dokumentpakke");
        ArchivedASiCE archivedASiCE = createASiCE.createAsice(databehandler, forsendelse);
        Sertifikat mottakerSertifikat = forsendelse.getTekniskMottaker().sertifikat;

        log.info("Creating CMS document");
        CMSDocument cms = createCMS.createCMS(archivedASiCE.getBytes(), mottakerSertifikat);

        Dokumentpakke dokumentpakke = new Dokumentpakke(cms.getBytes());

        return new Billable<>(dokumentpakke, archivedASiCE.getUnzippedContentBytesCount());
    }

}
