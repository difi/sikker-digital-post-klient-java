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

import no.difi.begrep.sdp.schema_v10.SDPFeil;
import no.difi.begrep.sdp.schema_v10.SDPFeiltype;
import no.difi.begrep.sdp.schema_v10.SDPKvittering;
import no.difi.begrep.sdp.schema_v10.SDPVarslingfeilet;
import no.difi.begrep.sdp.schema_v10.SDPVarslingskanal;
import no.difi.sdp.client.domain.Avsender;
import no.difi.sdp.client.domain.Feil;
import no.difi.sdp.client.domain.Feiltype;
import no.difi.sdp.client.domain.Prioritet;
import no.difi.sdp.client.domain.kvittering.AapningsKvittering;
import no.difi.sdp.client.domain.kvittering.BekreftelsesKvittering;
import no.difi.sdp.client.domain.kvittering.ForretningsKvittering;
import no.difi.sdp.client.domain.kvittering.LeveringsKvittering;
import no.difi.sdp.client.domain.kvittering.VarslingFeiletKvittering;
import no.difi.sdp.client.domain.kvittering.Varslingskanal;
import no.posten.dpost.offentlig.api.representations.EbmsAktoer;
import no.posten.dpost.offentlig.api.representations.EbmsApplikasjonsKvittering;
import no.posten.dpost.offentlig.api.representations.EbmsOutgoingMessage;
import no.posten.dpost.offentlig.api.representations.EbmsPullRequest;
import no.posten.dpost.offentlig.api.representations.Organisasjonsnummer;
import no.posten.dpost.offentlig.api.representations.SimpleStandardBusinessDocument;
import no.posten.dpost.offentlig.api.representations.StandardBusinessDocumentFactory;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;

import java.util.Date;

public class KvitteringBuilder {

    public EbmsPullRequest buildEbmsPullRequest(Organisasjonsnummer digitalpostMeldingsformidler, Prioritet prioritet) {
        EbmsAktoer meldingsformidler = EbmsAktoer.meldingsformidler(digitalpostMeldingsformidler);

        if (prioritet == Prioritet.PRIORITERT) {
            return new EbmsPullRequest(meldingsformidler, EbmsOutgoingMessage.Prioritet.PRIORITERT);
        }

        return new EbmsPullRequest(meldingsformidler);
    }

    public ForretningsKvittering buildForretningsKvittering(EbmsApplikasjonsKvittering applikasjonsKvittering) {
        String messageId = applikasjonsKvittering.messageId;
        String refToMessageId = applikasjonsKvittering.refToMessageId;
        SimpleStandardBusinessDocument sbd = applikasjonsKvittering.getStandardBusinessDocument();

        if (sbd.erKvittering()) {
            SimpleStandardBusinessDocument.SimpleKvittering kvittering = sbd.getKvittering();
            SDPKvittering sdpKvittering = kvittering.kvittering;

            String konversasjonsId = sbd.getConversationId();
            Date tidspunkt = sdpKvittering.getTidspunkt().toDate();

            if (sdpKvittering.getAapning() != null) {
                return AapningsKvittering.builder(tidspunkt, konversasjonsId, messageId, refToMessageId).build();
            } else if (sdpKvittering.getLevering() != null) {
                return LeveringsKvittering.builder(tidspunkt, konversasjonsId, messageId, refToMessageId).build();
            } else if (sdpKvittering.getVarslingfeilet() != null) {
                return varslingFeiletKvittering(sdpKvittering, konversasjonsId, tidspunkt, messageId, refToMessageId);
            }
        } else if (sbd.erFeil()) {
            return feil(sbd, messageId, refToMessageId);
        }
        //todo: proper exception handling
        throw new RuntimeException("Kvittering tilbake fra meldingsformidler var hverken kvittering eller feil.");
    }

    public EbmsApplikasjonsKvittering buildEbmsApplikasjonsKvittering(Avsender avsender, Organisasjonsnummer digitalpostMeldingsformidler, BekreftelsesKvittering bekreftelsesKvittering) {
        Organisasjonsnummer avsenderOrganisasjonsnummer = new Organisasjonsnummer(avsender.getOrganisasjonsnummer());

        //todo: riktig måte å sette konversasjonsid?
        StandardBusinessDocument doc = StandardBusinessDocumentFactory
                .create(avsenderOrganisasjonsnummer, digitalpostMeldingsformidler, "instanceIdentifier", bekreftelsesKvittering.getKonversasjonsId(), null);

        return EbmsApplikasjonsKvittering.create(EbmsAktoer.avsender(avsenderOrganisasjonsnummer), EbmsAktoer.meldingsformidler(digitalpostMeldingsformidler), doc)
                .withRefToMessageId(bekreftelsesKvittering.getRefToMessageId())
                .build();
    }

    private ForretningsKvittering feil(SimpleStandardBusinessDocument sbd, String messageId, String refToMessageId) {
        SDPFeil feil = sbd.getFeil();

        return Feil.builder(feil.getTidspunkt().toDate(), sbd.getConversationId(), messageId, refToMessageId, mapFeilType(feil.getFeiltype()))
                .detaljer(feil.getDetaljer())
                .build();
    }

    private ForretningsKvittering varslingFeiletKvittering(SDPKvittering sdpKvittering, String konversasjonsId, Date tidspunkt, String messageId, String refToMessageId) {
        SDPVarslingfeilet varslingfeilet = sdpKvittering.getVarslingfeilet();
        Varslingskanal varslingskanal = mapVarslingsKanal(varslingfeilet.getVarslingskanal());

        return VarslingFeiletKvittering.builder(tidspunkt, konversasjonsId, messageId, refToMessageId, varslingskanal)
                .beskrivelse(varslingfeilet.getBeskrivelse())
                .build();
    }

    private Feiltype mapFeilType(SDPFeiltype feiltype) {
        if (feiltype == SDPFeiltype.KLIENT) {
            return Feiltype.KLIENT;
        }
        return Feiltype.SERVER;
    }

    private Varslingskanal mapVarslingsKanal(SDPVarslingskanal varslingskanal) {
        if (varslingskanal == SDPVarslingskanal.EPOST) {
            return Varslingskanal.EPOST;
        }
        return Varslingskanal.SMS;
    }

}
