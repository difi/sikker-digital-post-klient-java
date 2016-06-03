package no.difi.sdp.client2.internal;

import no.difi.begrep.sdp.schema_v10.SDPFeiltype;
import no.difi.begrep.sdp.schema_v10.SDPKvittering;
import no.difi.begrep.sdp.schema_v10.SDPVarslingskanal;
import no.difi.sdp.client2.domain.exceptions.SikkerDigitalPostException;
import no.difi.sdp.client2.domain.kvittering.EbmsBekreftbar;
import no.difi.sdp.client2.domain.kvittering.Feil;
import no.difi.sdp.client2.domain.kvittering.ForretningsKvittering;
import no.difi.sdp.client2.domain.kvittering.KvitteringForespoersel;
import no.difi.sdp.client2.domain.kvittering.Kvitteringsinfo;
import no.difi.sdp.client2.domain.kvittering.LeveringsKvittering;
import no.difi.sdp.client2.domain.kvittering.VarslingFeiletKvittering;
import no.digipost.api.representations.EbmsApplikasjonsKvittering;
import no.digipost.api.representations.EbmsPullRequest;
import no.digipost.api.representations.Organisasjonsnummer;
import no.digipost.api.representations.SimpleStandardBusinessDocument;
import no.digipost.api.xml.Marshalling;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.xml.transform.StringResult;
import org.w3.xmldsig.Reference;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.time.Instant;
import java.util.List;

import static no.digipost.api.representations.EbmsAktoer.meldingsformidler;

public class KvitteringBuilder {

    public EbmsPullRequest buildEbmsPullRequest(Organisasjonsnummer meldingsformidler, KvitteringForespoersel kvitteringForespoersel) {
        return new EbmsPullRequest(meldingsformidler(meldingsformidler), kvitteringForespoersel.getPrioritet().getEbmsPrioritet(), kvitteringForespoersel.getMpcId());
    }

    public ForretningsKvittering buildForretningsKvittering(EbmsApplikasjonsKvittering applikasjonsKvittering) {
        SimpleStandardBusinessDocument sbd = applikasjonsKvittering.getStandardBusinessDocument();

        if (sbd.erKvittering()) {
            SDPKvittering sdpKvittering = sbd.getKvittering().kvittering;

            Kvitteringsinfo kvitteringsinfo = new Kvitteringsinfo();
            kvitteringsinfo.konversasjonsId = sbd.getConversationId();
            kvitteringsinfo.tidspunkt = Instant.ofEpochMilli(sbd.getKvittering().kvittering.getTidspunkt().getMillis());
            kvitteringsinfo.referanseTilMeldingId = applikasjonsKvittering.refToMessageId;

            EbmsBekreftbar ebmsBekreftbar = new EbmsBekreftbar() {
                @Override
                public String getMeldingsId() {
                    return applikasjonsKvittering.messageId;
                }

                @Override
                public String getReferanser() {
                    Reference reference = applikasjonsKvittering.references.get(0);
                    StringResult marshaledReferences = new StringResult();
                    Jaxb2Marshaller marshallerSingleton = Marshalling.getMarshallerSingleton();
                    Marshalling.marshal(marshallerSingleton, reference, marshaledReferences);
                    return marshaledReferences.toString();
                }
            };

            if (sdpKvittering.getAapning() != null) {
                throw new NotImplementedException();
//                return new AapningsKvittering(applikasjonsKvittering);
            } else if (sdpKvittering.getMottak() != null) {
                throw new NotImplementedException();
//            	return new MottaksKvittering(applikasjonsKvittering);
            } else if (sdpKvittering.getLevering() != null) {
//                throw new NotImplementedException();
                ebmsBekreftbar.getReferanser();
                return new LeveringsKvittering(ebmsBekreftbar, kvitteringsinfo);
            } else if (sdpKvittering.getVarslingfeilet() != null) {
//                return varslingFeiletKvittering(sdpKvittering, applikasjonsKvittering);
            } else if (sdpKvittering.getReturpost() != null) {
//            	return new ReturpostKvittering(applikasjonsKvittering);
            }
        } else if (sbd.erFeil()) {
            return feil(applikasjonsKvittering);
        }

        throw new SikkerDigitalPostException("Kvittering tilbake fra meldingsformidler var hverken kvittering eller feil.");
    }

    private ForretningsKvittering feil(EbmsApplikasjonsKvittering applikasjonsKvittering) {
        throw new NotImplementedException();

//        SDPFeil feil = applikasjonsKvittering.getStandardBusinessDocument().getFeil();
//
//        return Feil.builder(applikasjonsKvittering, mapFeilType(feil.getFeiltype()))
//                .detaljer(feil.getDetaljer())
//                .build();
    }

    private ForretningsKvittering varslingFeiletKvittering(SDPKvittering sdpKvittering, EbmsApplikasjonsKvittering applikasjonsKvittering) {
//        SDPVarslingfeilet varslingfeilet = sdpKvittering.getVarslingfeilet();
//        VarslingFeiletKvittering.Varslingskanal varslingskanal = mapVarslingsKanal(varslingfeilet.getVarslingskanal());
//
//        return VarslingFeiletKvittering.builder(applikasjonsKvittering, varslingskanal)
//                .beskrivelse(varslingfeilet.getBeskrivelse())
//                .build();
        throw new NotImplementedException();

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
