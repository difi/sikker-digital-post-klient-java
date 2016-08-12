package no.difi.sdp.client2.internal;

import no.difi.begrep.sdp.schema_v10.SDPDigitalPost;
import no.difi.sdp.client2.domain.Databehandler;
import no.difi.sdp.client2.domain.Forsendelse;
import no.difi.sdp.client2.domain.TekniskMottaker;
import no.digipost.api.representations.Dokumentpakke;
import no.digipost.api.representations.EbmsAktoer;
import no.digipost.api.representations.EbmsForsendelse;
import no.digipost.api.representations.Organisasjonsnummer;
import no.digipost.api.representations.StandardBusinessDocumentFactory;
import org.joda.time.DateTime;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;

import java.util.UUID;

public class EbmsForsendelseBuilder {

    private final SDPBuilder sdpBuilder;
    private final CreateDokumentpakke createDokumentpakke;

    public EbmsForsendelseBuilder() {
        sdpBuilder = new SDPBuilder();
        createDokumentpakke = new CreateDokumentpakke();
    }

    public EbmsForsendelse buildEbmsForsendelse(Databehandler databehandler, Organisasjonsnummer meldingsformidler, Forsendelse forsendelse) {
        TekniskMottaker mottaker = forsendelse.getTekniskMottaker();

        //EBMS
        EbmsAktoer ebmsAvsender = EbmsAktoer.avsender(databehandler.organisasjonsnummer.getOrganisasjonsnummer());
        EbmsAktoer ebmsMottaker = EbmsAktoer.meldingsformidler(meldingsformidler);

        //SBD
        String meldingsId = UUID.randomUUID().toString();
        Organisasjonsnummer sbdhMottaker = mottaker.organisasjonsnummer;
        Organisasjonsnummer sbdhAvsender = Organisasjonsnummer.of(databehandler.organisasjonsnummer.getOrganisasjonsnummer());
        SDPDigitalPost sikkerDigitalPost = sdpBuilder.buildDigitalPost(forsendelse);
        StandardBusinessDocument standardBusinessDocument = StandardBusinessDocumentFactory.create(sbdhAvsender, sbdhMottaker, meldingsId, DateTime.now(), forsendelse.getKonversasjonsId(), sikkerDigitalPost);

        //Dokumentpakke
        Dokumentpakke dokumentpakke = createDokumentpakke.createDokumentpakke(databehandler, forsendelse);

        return EbmsForsendelse.create(ebmsAvsender, ebmsMottaker, sbdhMottaker, standardBusinessDocument, dokumentpakke)
                .withPrioritet(forsendelse.getPrioritet().getEbmsPrioritet())
                .withMpcId(forsendelse.getMpcId())
                .withAction(forsendelse.type.action)
                .build();
    }

}
