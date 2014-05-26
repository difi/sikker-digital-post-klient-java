package no.difi.sdp.client.internal;

import no.difi.begrep.sdp.schema_v10.*;
import no.difi.sdp.client.domain.Feil;
import no.difi.sdp.client.domain.Feiltype;
import no.difi.sdp.client.domain.Prioritet;
import no.difi.sdp.client.domain.kvittering.*;
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
        String refToMessageId = applikasjonsKvittering.refToMessageId;
        SimpleStandardBusinessDocument sbd = applikasjonsKvittering.getStandardBusinessDocument();

        if (sbd.erKvittering()) {
            SimpleStandardBusinessDocument.SimpleKvittering kvittering = sbd.getKvittering();
            SDPKvittering sdpKvittering = kvittering.kvittering;

            sbd.getCorrelationId();
            //String konversasjonsId = sbd.getCorrelationInformation().getRequestingDocumentInstanceIdentifier();
            String konversasjonsId = "todo";
            Date tidspunkt = sdpKvittering.getTidspunkt().toDate();

            if (sdpKvittering.getAapning() != null) {
                return AapningsKvittering.builder(tidspunkt, konversasjonsId, refToMessageId).build();
            } else if (sdpKvittering.getLevering() != null) {
                return LeveringsKvittering.builder(tidspunkt, konversasjonsId, refToMessageId).build();
            } else if (sdpKvittering.getTilbaketrekking() != null) {
                return tilbaketrekkingsKvittering(sdpKvittering, konversasjonsId, tidspunkt, refToMessageId);
            } else if (sdpKvittering.getVarslingfeilet() != null) {
                return varslingFeiletKvittering(sdpKvittering, konversasjonsId, tidspunkt, refToMessageId);
            }
        } else if (sbd.erFeil()) {
            return feil(sbd, refToMessageId);
        }
        //todo: returnere message id
        //todo: proper exception handling
        throw new RuntimeException("Kvittering tilbake fra meldingsformidler var hverken kvittering eller feil.");
    }

    public EbmsApplikasjonsKvittering buildEbmsApplikasjonsKvittering(BekreftelsesKvittering bekreftelsesKvittering) {
        //todo
        return null;
    }

    private ForretningsKvittering feil(SimpleStandardBusinessDocument sbd, String refToMessageId) {
        SDPFeil feil = sbd.getFeil();

        return Feil.builder(feil.getTidspunkt().toDate(), "todo", refToMessageId, mapFeilType(feil.getFeiltype()))
                .detaljer(feil.getDetaljer())
                .build();
    }

    private ForretningsKvittering tilbaketrekkingsKvittering(SDPKvittering sdpKvittering, String konversasjonsId, Date tidspunkt, String refToMessageId) {
        SDPTilbaketrekkingsresultat tilbaketrekking = sdpKvittering.getTilbaketrekking();
        TilbaketrekkingsStatus status = mapTilbaketrekkingsStatus(tilbaketrekking.getStatus());

        return TilbaketrekkingsKvittering.builder(tidspunkt, konversasjonsId, refToMessageId, status)
                .beskrivelse(tilbaketrekking.getBeskrivelse())
                .build();
    }

    private ForretningsKvittering varslingFeiletKvittering(SDPKvittering sdpKvittering, String konversasjonsId, Date tidspunkt, String refToMessageId) {
        SDPVarslingfeilet varslingfeilet = sdpKvittering.getVarslingfeilet();
        Varslingskanal varslingskanal = mapVarslingsKanal(varslingfeilet.getVarslingskanal());

        return VarslingFeiletKvittering.builder(tidspunkt, konversasjonsId, refToMessageId, varslingskanal)
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

    private TilbaketrekkingsStatus mapTilbaketrekkingsStatus(SDPTilbaketrekkingsstatus status) {
        if (status == SDPTilbaketrekkingsstatus.OK) {
            return TilbaketrekkingsStatus.OK;
        }
        return TilbaketrekkingsStatus.FEILET;
    }
}
