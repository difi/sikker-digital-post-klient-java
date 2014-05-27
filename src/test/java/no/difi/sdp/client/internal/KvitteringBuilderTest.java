package no.difi.sdp.client.internal;

import no.difi.begrep.sdp.schema_v10.*;
import no.difi.sdp.client.domain.Feil;
import no.difi.sdp.client.domain.Feiltype;
import no.difi.sdp.client.domain.Prioritet;
import no.difi.sdp.client.domain.kvittering.*;
import no.posten.dpost.offentlig.api.representations.EbmsApplikasjonsKvittering;
import no.posten.dpost.offentlig.api.representations.EbmsOutgoingMessage;
import no.posten.dpost.offentlig.api.representations.EbmsPullRequest;
import no.posten.dpost.offentlig.api.representations.Organisasjonsnummer;
import org.joda.time.DateTime;
import org.junit.Test;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocumentHeader;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

public class KvitteringBuilderTest {

    private final KvitteringBuilder kvitteringBuilder = new KvitteringBuilder();

    @Test
    public void shoud_build_pull_request_with_standard_priority() {
        EbmsPullRequest ebmsPullRequest = kvitteringBuilder.buildEbmsPullRequest(new Organisasjonsnummer("123"), Prioritet.NORMAL);

        assertThat(ebmsPullRequest.getEbmsMottaker().orgnr.toString()).isEqualTo("123");
        assertThat(ebmsPullRequest.prioritet).isEqualTo(EbmsOutgoingMessage.Prioritet.NORMAL);
    }

    @Test
    public void shoud_build_pull_request_with_standard_high_priority() {
        EbmsPullRequest ebmsPullRequest = kvitteringBuilder.buildEbmsPullRequest(new Organisasjonsnummer("123"), Prioritet.PRIORITERT);

        assertThat(ebmsPullRequest.getEbmsMottaker().orgnr.toString()).isEqualTo("123");
        assertThat(ebmsPullRequest.prioritet).isEqualTo(EbmsOutgoingMessage.Prioritet.PRIORITERT);
    }

    @Test
    public void should_build_aapnings_kvittering() {
        EbmsApplikasjonsKvittering ebmsKvittering = createEbmsAapningsKvittering();

        AapningsKvittering aapningKvittering = (AapningsKvittering) kvitteringBuilder.buildForretningsKvittering(ebmsKvittering);

        assertNotNull(aapningKvittering.getKonversasjonsId());
        assertNotNull(aapningKvittering.getTidspunkt());
    }

    @Test
    public void should_build_leverings_kvittering() {
        EbmsApplikasjonsKvittering ebmsKvittering = createEbmsLeveringsKvittering();

        LeveringsKvittering leveringsKvittering = (LeveringsKvittering) kvitteringBuilder.buildForretningsKvittering(ebmsKvittering);

        assertNotNull(leveringsKvittering.getKonversasjonsId());
        assertNotNull(leveringsKvittering.getTidspunkt());
    }

    @Test
    public void should_build_varsling_feilet_epost_kvittering() {
        EbmsApplikasjonsKvittering ebmsKvittering = createEbmsVarslingFeiletKvittering(SDPVarslingskanal.EPOST);

        VarslingFeiletKvittering varslingFeiletKvittering = (VarslingFeiletKvittering) kvitteringBuilder.buildForretningsKvittering(ebmsKvittering);

        assertNotNull(varslingFeiletKvittering.getKonversasjonsId());
        assertNotNull(varslingFeiletKvittering.getTidspunkt());

        assertThat(varslingFeiletKvittering.getBeskrivelse()).isEqualTo("Varsling feilet 'Viktig brev'");
        assertThat(varslingFeiletKvittering.getVarslingskanal()).isEqualTo(Varslingskanal.EPOST);
    }

    @Test
    public void should_build_varsling_feilet_sms_kvittering() {
        EbmsApplikasjonsKvittering ebmsKvittering = createEbmsVarslingFeiletKvittering(SDPVarslingskanal.SMS);

        VarslingFeiletKvittering varslingFeiletKvittering = (VarslingFeiletKvittering) kvitteringBuilder.buildForretningsKvittering(ebmsKvittering);

        assertNotNull(varslingFeiletKvittering.getKonversasjonsId());
        assertNotNull(varslingFeiletKvittering.getTidspunkt());
        assertThat((varslingFeiletKvittering).getBeskrivelse()).isEqualTo("Varsling feilet 'Viktig brev'");
        assertThat((varslingFeiletKvittering).getVarslingskanal()).isEqualTo(Varslingskanal.SMS);
    }

    @Test
    public void should_build_tilbaketrekking_ok_kvittering() {
        EbmsApplikasjonsKvittering ebmsKvittering = createEbmsTilbaketrekkingsKvittering(SDPTilbaketrekkingsstatus.OK);

        TilbaketrekkingsKvittering tilbaketrekkingsKvittering = (TilbaketrekkingsKvittering) kvitteringBuilder.buildForretningsKvittering(ebmsKvittering);

        assertNotNull(tilbaketrekkingsKvittering.getKonversasjonsId());
        assertNotNull(tilbaketrekkingsKvittering.getTidspunkt());
        assertThat(tilbaketrekkingsKvittering.getBeskrivelse()).isEqualTo("Tilbaketrekking av 'Viktig brev'");
        assertThat(tilbaketrekkingsKvittering.getStatus()).isEqualTo(TilbaketrekkingsStatus.OK);
    }

    @Test
    public void should_build_tilbaketrekking_feilet_kvittering() {
        EbmsApplikasjonsKvittering ebmsKvittering = createEbmsTilbaketrekkingsKvittering(SDPTilbaketrekkingsstatus.FEILET);

        TilbaketrekkingsKvittering tilbaketrekkingsKvittering = (TilbaketrekkingsKvittering) kvitteringBuilder.buildForretningsKvittering(ebmsKvittering);

        assertNotNull(tilbaketrekkingsKvittering.getKonversasjonsId());
        assertNotNull(tilbaketrekkingsKvittering.getTidspunkt());
        assertThat((tilbaketrekkingsKvittering).getBeskrivelse()).isEqualTo("Tilbaketrekking av 'Viktig brev'");
        assertThat((tilbaketrekkingsKvittering).getStatus()).isEqualTo(TilbaketrekkingsStatus.FEILET);
    }

    @Test
    public void should_build_klient_feil() {
        EbmsApplikasjonsKvittering ebmsKvittering = createEbmsFeil(SDPFeiltype.KLIENT);

        Feil feil = (Feil) kvitteringBuilder.buildForretningsKvittering(ebmsKvittering);

        assertNotNull(feil.getKonversasjonsId());
        assertNotNull(feil.getTidspunkt());
        assertThat(feil.getFeiltype()).isEqualTo(Feiltype.KLIENT);
        assertThat(feil.getDetaljer()).isEqualTo("Feilinformasjon");
    }

    @Test
    public void should_build_server_feil() {
        EbmsApplikasjonsKvittering ebmsKvittering = createEbmsFeil(SDPFeiltype.SERVER);

        Feil feil = (Feil) kvitteringBuilder.buildForretningsKvittering(ebmsKvittering);

        assertNotNull(feil.getKonversasjonsId());
        assertNotNull(feil.getTidspunkt());
        assertThat(feil.getFeiltype()).isEqualTo(Feiltype.SERVER);
        assertThat(feil.getDetaljer()).isEqualTo("Feilinformasjon");
    }

    private EbmsApplikasjonsKvittering createEbmsFeil(SDPFeiltype feiltype) {
        SDPFeil sdpFeil = new SDPFeil(null, DateTime.now(), feiltype, "Feilinformasjon");
        return createEbmsKvittering(sdpFeil);
    }

    private EbmsApplikasjonsKvittering createEbmsAapningsKvittering() {
        SDPKvittering aapningsKvittering = new SDPKvittering(null, DateTime.now(), null, null, new SDPAapning(), null);
        return createEbmsKvittering(aapningsKvittering);
    }

    private EbmsApplikasjonsKvittering createEbmsLeveringsKvittering() {
        SDPKvittering leveringsKvittering = new SDPKvittering(null, DateTime.now(), null, null, null, new SDPLevering());
        return createEbmsKvittering(leveringsKvittering);
    }

    private EbmsApplikasjonsKvittering createEbmsVarslingFeiletKvittering(SDPVarslingskanal varslingskanal) {
        SDPVarslingfeilet sdpVarslingfeilet = new SDPVarslingfeilet(varslingskanal, "Varsling feilet 'Viktig brev'");
        SDPKvittering varslingFeiletKvittering = new SDPKvittering(null, DateTime.now(), null, sdpVarslingfeilet, null, null);
        return createEbmsKvittering(varslingFeiletKvittering);
    }

    private EbmsApplikasjonsKvittering createEbmsTilbaketrekkingsKvittering(SDPTilbaketrekkingsstatus status) {
        SDPTilbaketrekkingsresultat sdpTilbaketrekkingsresultat = new SDPTilbaketrekkingsresultat(status, "Tilbaketrekking av 'Viktig brev'");
        SDPKvittering tilbaketrekkingsKvittering = new SDPKvittering(null, DateTime.now(), sdpTilbaketrekkingsresultat, null, null, null);
        return createEbmsKvittering(tilbaketrekkingsKvittering);
    }

    private EbmsApplikasjonsKvittering createEbmsKvittering(Object sdpMelding) {
        Organisasjonsnummer avsender = new Organisasjonsnummer("123");
        Organisasjonsnummer mottaker = new Organisasjonsnummer("456");

        StandardBusinessDocument sbd = new StandardBusinessDocument(new StandardBusinessDocumentHeader(), sdpMelding);

        return EbmsApplikasjonsKvittering.create(avsender, mottaker, sbd).build();
    }

}
