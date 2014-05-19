package no.difi.sdp.client.internal;

import no.difi.begrep.sdp.schema_v10.SDPFeil;
import no.difi.begrep.sdp.schema_v10.SDPKvittering;
import no.difi.begrep.sdp.schema_v10.SDPTilbaketrekkingsresultat;
import no.difi.begrep.sdp.schema_v10.SDPTilbaketrekkingsstatus;
import no.difi.begrep.sdp.schema_v10.SDPVarslingfeilet;
import no.difi.begrep.sdp.schema_v10.SDPVarslingskanal;
import no.difi.sdp.client.domain.Prioritet;
import no.difi.sdp.client.domain.kvittering.AapningsKvittering;
import no.difi.sdp.client.domain.kvittering.ForretningsKvittering;
import no.difi.sdp.client.domain.kvittering.LeveringsKvittering;
import no.difi.sdp.client.domain.kvittering.TilbaketrekkingsKvittering;
import no.difi.sdp.client.domain.kvittering.TilbaketrekkingsStatus;
import no.difi.sdp.client.domain.kvittering.VarslingFeiletKvittering;
import no.difi.sdp.client.domain.kvittering.Varslingskanal;
import no.posten.dpost.offentlig.api.representations.*;

import java.util.Date;

public class KvitteringBuilder {

    public EbmsPullRequest buildEbmsPullRequest(Organisasjonsnummer meldingsformidlerOrgNummer, Prioritet prioritet) {
        EbmsMottaker meldingsformidler = new EbmsMottaker(meldingsformidlerOrgNummer);

        if (prioritet == Prioritet.PRIORITERT) {
            return new EbmsPullRequest(meldingsformidler, EbmsOutgoingMessage.Prioritet.PRIORITERT);
        }

        return new EbmsPullRequest(meldingsformidler);
    }

    public ForretningsKvittering buildForretningsKvittering(EbmsApplikasjonsKvittering applikasjonsKvittering) {
        Object sdpMelding = applikasjonsKvittering.sbd.getAny();

        if (sdpMelding instanceof SDPKvittering) {
            SDPKvittering sdpKvittering = (SDPKvittering) sdpMelding;
            String konversasjonsId = sdpKvittering.getKonversasjonsId();
            Date tidspunkt = sdpKvittering.getTidspunkt().toDate();

            if (sdpKvittering.getAapning() != null) {
                return AapningsKvittering.builder(tidspunkt, konversasjonsId).build();
            } else if (sdpKvittering.getLevering() != null) {
                return LeveringsKvittering.builder(tidspunkt, konversasjonsId).build();
            } else if (sdpKvittering.getTilbaketrekking() != null) {
                SDPTilbaketrekkingsresultat tilbaketrekking = sdpKvittering.getTilbaketrekking();
                TilbaketrekkingsStatus status = mapTilbaketrekkingsStatus(tilbaketrekking.getStatus());

                return TilbaketrekkingsKvittering.builder(tidspunkt, konversasjonsId, status)
                        .beskrivelse(tilbaketrekking.getBeskrivelse())
                        .build();
            } else if (sdpKvittering.getVarslingfeilet() != null) {
                SDPVarslingfeilet varslingfeilet = sdpKvittering.getVarslingfeilet();
                Varslingskanal varslingskanal = mapVarslingsKanal(varslingfeilet.getVarslingskanal());

                return VarslingFeiletKvittering.builder(tidspunkt, konversasjonsId, varslingskanal)
                        .feilbeskrivelse(varslingfeilet.getBeskrivelse())
                        .build();
            }
        } else if (sdpMelding instanceof SDPFeil) {
            //todo
        }
        return null;
    }

    private Varslingskanal mapVarslingsKanal(SDPVarslingskanal varslingskanal) {
        if (varslingskanal == SDPVarslingskanal.EPOST) {
            return Varslingskanal.EPOST;
        }
        return Varslingskanal.SMS;
    }

    private TilbaketrekkingsStatus mapTilbaketrekkingsStatus(SDPTilbaketrekkingsstatus status) {
        if (status == SDPTilbaketrekkingsstatus.OK) {
            return TilbaketrekkingsStatus.OK;
        }
        return TilbaketrekkingsStatus.FEILET;
    }
}
