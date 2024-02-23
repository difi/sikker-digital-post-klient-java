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

import no.difi.begrep.sdp.schema_v10.SDPFeil;
import no.difi.begrep.sdp.schema_v10.SDPFeiltype;
import no.difi.begrep.sdp.schema_v10.SDPKvittering;
import no.difi.begrep.sdp.schema_v10.SDPVarslingfeilet;
import no.difi.begrep.sdp.schema_v10.SDPVarslingskanal;
import no.difi.sdp.client2.domain.exceptions.SikkerDigitalPostException;
import no.difi.sdp.client2.domain.kvittering.AapningsKvittering;
import no.difi.sdp.client2.domain.kvittering.Feil;
import no.difi.sdp.client2.domain.kvittering.ForretningsKvittering;
import no.difi.sdp.client2.domain.kvittering.KvitteringForespoersel;
import no.difi.sdp.client2.domain.kvittering.KvitteringsInfo;
import no.difi.sdp.client2.domain.kvittering.LeveringsKvittering;
import no.difi.sdp.client2.domain.kvittering.MottaksKvittering;
import no.difi.sdp.client2.domain.kvittering.ReturpostKvittering;
import no.difi.sdp.client2.domain.kvittering.VarslingFeiletKvittering;
import no.digipost.api.representations.EbmsApplikasjonsKvittering;
import no.digipost.api.representations.EbmsPullRequest;
import no.digipost.api.representations.Organisasjonsnummer;
import no.digipost.api.representations.SimpleStandardBusinessDocument;

import static no.digipost.api.representations.EbmsAktoer.meldingsformidler;

public class KvitteringBuilder {

    public EbmsPullRequest buildEbmsPullRequest(Organisasjonsnummer meldingsformidler, KvitteringForespoersel kvitteringForespoersel) {
        return new EbmsPullRequest(meldingsformidler(meldingsformidler), kvitteringForespoersel.getPrioritet().getEbmsPrioritet(), kvitteringForespoersel.getMpcId());
    }

    public ForretningsKvittering buildForretningsKvittering(EbmsApplikasjonsKvittering ebmsApplikasjonsKvittering) {
        SimpleStandardBusinessDocument simpleStandardBusinessDocument = ebmsApplikasjonsKvittering.getStandardBusinessDocument();

        KvitteringsInfo.Builder kvitteringsinfoBuilder = KvitteringsInfo.builder()
                .konversasjonsId(simpleStandardBusinessDocument.getConversationId())
                .referanseTilMeldingId(ebmsApplikasjonsKvittering.refToMessageId);

        if (simpleStandardBusinessDocument.erKvittering()) {
            SDPKvittering sdpKvittering = simpleStandardBusinessDocument.getKvittering().kvittering;
            kvitteringsinfoBuilder.tidspunkt(sdpKvittering.getTidspunkt().toInstant());

            final KvitteringsInfo kvitteringsInfo = kvitteringsinfoBuilder.build();

            if (sdpKvittering.getAapning() != null) {
                return new AapningsKvittering(ebmsApplikasjonsKvittering, kvitteringsInfo);
            } else if (sdpKvittering.getMottak() != null) {
                return new MottaksKvittering(ebmsApplikasjonsKvittering, kvitteringsInfo);
            } else if (sdpKvittering.getLevering() != null) {
                return new LeveringsKvittering(ebmsApplikasjonsKvittering, kvitteringsInfo);
            } else if (sdpKvittering.getVarslingfeilet() != null) {
                return varslingFeiletKvittering(sdpKvittering, kvitteringsInfo, ebmsApplikasjonsKvittering);
            } else if (sdpKvittering.getReturpost() != null) {
                return new ReturpostKvittering(ebmsApplikasjonsKvittering, kvitteringsInfo);
            }
        } else if (simpleStandardBusinessDocument.erFeil()) {
            SDPFeil sdpFeil = simpleStandardBusinessDocument.getFeil();
            kvitteringsinfoBuilder.tidspunkt(sdpFeil.getTidspunkt().toInstant());

            final KvitteringsInfo kvitteringsInfo = kvitteringsinfoBuilder.build();

            return feil(ebmsApplikasjonsKvittering, kvitteringsInfo);
        }

        throw new SikkerDigitalPostException("Kvittering tilbake fra meldingsformidler var verken kvittering eller feil.");
    }

    private ForretningsKvittering feil(EbmsApplikasjonsKvittering ebmsApplikasjonsKvittering, KvitteringsInfo kvitteringsInfo) {
        SDPFeil feil = ebmsApplikasjonsKvittering.getStandardBusinessDocument().getFeil();

        return Feil.builder(ebmsApplikasjonsKvittering, kvitteringsInfo, mapFeilType(feil.getFeiltype()))
                .detaljer(feil.getDetaljer())
                .build();
    }

    private ForretningsKvittering varslingFeiletKvittering(SDPKvittering sdpKvittering, KvitteringsInfo kvitteringsInfo, EbmsApplikasjonsKvittering ebmsAapplikasjonsKvittering) {
        SDPVarslingfeilet varslingfeilet = sdpKvittering.getVarslingfeilet();
        VarslingFeiletKvittering.Varslingskanal varslingskanal = mapVarslingsKanal(varslingfeilet.getVarslingskanal());

        return VarslingFeiletKvittering.builder(ebmsAapplikasjonsKvittering, kvitteringsInfo, varslingskanal)
                .beskrivelse(varslingfeilet.getBeskrivelse())
                .build();
    }

    private Feil.Feiltype mapFeilType(SDPFeiltype feiltype) {
        if (feiltype == SDPFeiltype.KLIENT) {
            return Feil.Feiltype.KLIENT;
        }
        return Feil.Feiltype.SERVER;
    }

    private VarslingFeiletKvittering.Varslingskanal mapVarslingsKanal(SDPVarslingskanal varslingskanal) {
        if (varslingskanal == SDPVarslingskanal.EPOST) {
            return VarslingFeiletKvittering.Varslingskanal.EPOST;
        }
        return VarslingFeiletKvittering.Varslingskanal.SMS;
    }

}
