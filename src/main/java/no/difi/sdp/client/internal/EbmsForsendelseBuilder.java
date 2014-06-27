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

import no.difi.begrep.sdp.schema_v10.SDPDigitalPost;
import no.difi.sdp.client.domain.Forsendelse;
import no.difi.sdp.client.domain.Mottaker;
import no.difi.sdp.client.domain.TekniskAvsender;
import no.digipost.api.representations.Dokumentpakke;
import no.digipost.api.representations.EbmsAktoer;
import no.digipost.api.representations.EbmsForsendelse;
import no.digipost.api.representations.Organisasjonsnummer;
import no.digipost.api.representations.StandardBusinessDocumentFactory;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;

import java.util.UUID;

public class EbmsForsendelseBuilder {

    private final SDPBuilder sdpBuilder;
    private final CreateDokumentpakke createDokumentpakke;

    public EbmsForsendelseBuilder() {
        sdpBuilder = new SDPBuilder();
        createDokumentpakke = new CreateDokumentpakke();
    }

    public EbmsForsendelse buildEbmsForsendelse(TekniskAvsender tekniskAvsender, Organisasjonsnummer meldingsformidler, Forsendelse forsendelse) {
        Mottaker mottaker = forsendelse.getDigitalPost().getMottaker();

        //EBMS
        EbmsAktoer ebmsAvsender = EbmsAktoer.avsender(tekniskAvsender.getOrganisasjonsnummer());
        EbmsAktoer ebmsMottaker = EbmsAktoer.meldingsformidler(meldingsformidler);

        //SBD
        String meldingsId = UUID.randomUUID().toString();
        Organisasjonsnummer sbdhMottaker = new Organisasjonsnummer(mottaker.getOrganisasjonsnummerPostkasse());
        Organisasjonsnummer sbdhAvsender = new Organisasjonsnummer(tekniskAvsender.getOrganisasjonsnummer());
        SDPDigitalPost sikkerDigitalPost = sdpBuilder.buildDigitalPost(forsendelse);
        StandardBusinessDocument standardBusinessDocument = StandardBusinessDocumentFactory.create(sbdhAvsender, sbdhMottaker, meldingsId, forsendelse.getKonversasjonsId(), sikkerDigitalPost);

        //Dokumentpakke
        Dokumentpakke dokumentpakke = createDokumentpakke.createDokumentpakke(tekniskAvsender, forsendelse);

        return EbmsForsendelse.create(ebmsAvsender, ebmsMottaker, sbdhMottaker, standardBusinessDocument, dokumentpakke)
                .withPrioritet(forsendelse.getPrioritet().getEbmsPrioritet())
                .withMpcId(forsendelse.getMpcId())
                .build();
    }

}
