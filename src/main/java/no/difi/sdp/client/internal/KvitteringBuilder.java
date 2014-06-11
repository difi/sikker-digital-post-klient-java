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
import no.difi.sdp.client.domain.kvittering.Feil;
import no.difi.sdp.client.domain.Prioritet;
import no.difi.sdp.client.domain.kvittering.AapningsKvittering;
import no.difi.sdp.client.domain.kvittering.ForretningsKvittering;
import no.difi.sdp.client.domain.kvittering.LeveringsKvittering;
import no.difi.sdp.client.domain.kvittering.VarslingFeiletKvittering;
import no.digipost.api.representations.EbmsApplikasjonsKvittering;
import no.digipost.api.representations.EbmsPullRequest;
import no.digipost.api.representations.Organisasjonsnummer;
import no.digipost.api.representations.SimpleStandardBusinessDocument;

import static no.digipost.api.representations.EbmsAktoer.meldingsformidler;

public class KvitteringBuilder {

    public EbmsPullRequest buildEbmsPullRequest(Organisasjonsnummer digitalpostMeldingsformidler, Prioritet prioritet) {
        return new EbmsPullRequest(meldingsformidler(digitalpostMeldingsformidler), prioritet.getEbmsPrioritet());
    }

    public ForretningsKvittering buildForretningsKvittering(EbmsApplikasjonsKvittering applikasjonsKvittering) {
        SimpleStandardBusinessDocument sbd = applikasjonsKvittering.getStandardBusinessDocument();

        if (sbd.erKvittering()) {
            SDPKvittering sdpKvittering = sbd.getKvittering().kvittering;

            if (sdpKvittering.getAapning() != null) {
                return AapningsKvittering.builder(applikasjonsKvittering).build();
            } else if (sdpKvittering.getLevering() != null) {
                return LeveringsKvittering.builder(applikasjonsKvittering).build();
            } else if (sdpKvittering.getVarslingfeilet() != null) {
                return varslingFeiletKvittering(sdpKvittering, applikasjonsKvittering);
            }
        } else if (sbd.erFeil()) {
            return feil(applikasjonsKvittering);
        }
        //todo: proper exception handling
        throw new RuntimeException("Kvittering tilbake fra meldingsformidler var hverken kvittering eller feil.");
    }

    private ForretningsKvittering feil(EbmsApplikasjonsKvittering applikasjonsKvittering) {
        SDPFeil feil = applikasjonsKvittering.getStandardBusinessDocument().getFeil();

        return Feil.builder(applikasjonsKvittering, mapFeilType(feil.getFeiltype()))
                .detaljer(feil.getDetaljer())
                .build();
    }

    private ForretningsKvittering varslingFeiletKvittering(SDPKvittering sdpKvittering, EbmsApplikasjonsKvittering applikasjonsKvittering) {
        SDPVarslingfeilet varslingfeilet = sdpKvittering.getVarslingfeilet();
        VarslingFeiletKvittering.Varslingskanal varslingskanal = mapVarslingsKanal(varslingfeilet.getVarslingskanal());

        return VarslingFeiletKvittering.builder(applikasjonsKvittering, varslingskanal)
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
