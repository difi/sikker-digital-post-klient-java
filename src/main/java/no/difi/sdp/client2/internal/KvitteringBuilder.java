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
import no.difi.sdp.client2.domain.kvittering.KvitteringsInfo2;
import no.difi.sdp.client2.domain.kvittering.LeveringsKvittering;
import no.difi.sdp.client2.domain.kvittering.MottaksKvittering;
import no.difi.sdp.client2.domain.kvittering.ReturpostKvittering;
import no.difi.sdp.client2.domain.kvittering.VarslingFeiletKvittering;
import no.digipost.api.representations.EbmsApplikasjonsKvittering;
import no.digipost.api.representations.EbmsPullRequest;
import no.digipost.api.representations.Organisasjonsnummer;
import no.digipost.api.representations.SimpleStandardBusinessDocument;

import java.time.Instant;

import static no.digipost.api.representations.EbmsAktoer.meldingsformidler;

public class KvitteringBuilder {

    public EbmsPullRequest buildEbmsPullRequest(Organisasjonsnummer meldingsformidler, KvitteringForespoersel kvitteringForespoersel) {
        return new EbmsPullRequest(meldingsformidler(meldingsformidler), kvitteringForespoersel.getPrioritet().getEbmsPrioritet(), kvitteringForespoersel.getMpcId());
    }

    public ForretningsKvittering buildForretningsKvittering(EbmsApplikasjonsKvittering ebmsApplikasjonsKvittering) {
        SimpleStandardBusinessDocument simpleStandardBusinessDocument = ebmsApplikasjonsKvittering.getStandardBusinessDocument();

        KvitteringsInfo2.Builder kvitteringsinfoBuilder = KvitteringsInfo2.builder()
                .konversasjonsId(simpleStandardBusinessDocument.getConversationId())
                .referanseTilMeldingId(ebmsApplikasjonsKvittering.refToMessageId);

        if (simpleStandardBusinessDocument.erKvittering()) {
            SDPKvittering sdpKvittering = simpleStandardBusinessDocument.getKvittering().kvittering;
            kvitteringsinfoBuilder.tidspunkt(Instant.ofEpochMilli(sdpKvittering.getTidspunkt().getMillis()));

            final KvitteringsInfo2 kvitteringsInfo2 = kvitteringsinfoBuilder.build();

            if (sdpKvittering.getAapning() != null) {
                return new AapningsKvittering(ebmsApplikasjonsKvittering, kvitteringsInfo2);
            } else if (sdpKvittering.getMottak() != null) {
                return new MottaksKvittering(ebmsApplikasjonsKvittering, kvitteringsInfo2);
            } else if (sdpKvittering.getLevering() != null) {
                return new LeveringsKvittering(ebmsApplikasjonsKvittering, kvitteringsInfo2);
            } else if (sdpKvittering.getVarslingfeilet() != null) {
                return varslingFeiletKvittering(sdpKvittering, kvitteringsInfo2, ebmsApplikasjonsKvittering);
            } else if (sdpKvittering.getReturpost() != null) {
                return new ReturpostKvittering(ebmsApplikasjonsKvittering, kvitteringsInfo2);
            }
        } else if (simpleStandardBusinessDocument.erFeil()) {
            SDPFeil sdpFeil = simpleStandardBusinessDocument.getFeil();
            kvitteringsinfoBuilder.tidspunkt(Instant.ofEpochMilli(sdpFeil.getTidspunkt().getMillis()));

            final KvitteringsInfo2 kvitteringsInfo2 = kvitteringsinfoBuilder.build();

            return feil(ebmsApplikasjonsKvittering, kvitteringsInfo2);
        }

        throw new SikkerDigitalPostException("Kvittering tilbake fra meldingsformidler var verken kvittering eller feil.");
    }

    private ForretningsKvittering feil(EbmsApplikasjonsKvittering ebmsApplikasjonsKvittering, KvitteringsInfo2 kvitteringsInfo2) {
        SDPFeil feil = ebmsApplikasjonsKvittering.getStandardBusinessDocument().getFeil();

        return Feil.builder(ebmsApplikasjonsKvittering, kvitteringsInfo2, mapFeilType(feil.getFeiltype()))
                .detaljer(feil.getDetaljer())
                .build();
    }

    private ForretningsKvittering varslingFeiletKvittering(SDPKvittering sdpKvittering, KvitteringsInfo2 kvitteringsInfo2, EbmsApplikasjonsKvittering ebmsAapplikasjonsKvittering) {
        SDPVarslingfeilet varslingfeilet = sdpKvittering.getVarslingfeilet();
        VarslingFeiletKvittering.Varslingskanal varslingskanal = mapVarslingsKanal(varslingfeilet.getVarslingskanal());

        return VarslingFeiletKvittering.builder(ebmsAapplikasjonsKvittering, kvitteringsInfo2, varslingskanal)
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
