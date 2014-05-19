package no.difi.sdp.client.internal;

import no.difi.begrep.sdp.schema_v10.*;
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

import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class KvitteringBuilderTest {

    private final KvitteringBuilder kvitteringBuilder = new KvitteringBuilder();

    @Test
    public void shoud_build_pull_request_with_standard_priority() {
        EbmsPullRequest ebmsPullRequest = kvitteringBuilder.buildEbmsPullRequest(new Organisasjonsnummer("123"), Prioritet.NORMAL);

        assertThat(ebmsPullRequest.getEbmsMottaker().orgnr.toString()).isEqualTo("123");
        assertThat(ebmsPullRequest.prioritet).isEqualTo(EbmsOutgoingMessage.Prioritet.STANDARD);
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

        ForretningsKvittering forretningsKvittering = kvitteringBuilder.buildForretningsKvittering(ebmsKvittering);

        assertTrue(forretningsKvittering instanceof VarslingFeiletKvittering);
        assertNotNull(forretningsKvittering.getKonversasjonsId());
        assertNotNull(forretningsKvittering.getTidspunkt());
        assertThat(((VarslingFeiletKvittering)forretningsKvittering).getBeskrivelse()).isEqualTo("Varsling feilet 'Viktig brev'");
        assertThat(((VarslingFeiletKvittering)forretningsKvittering).getVarslingskanal()).isEqualTo(Varslingskanal.SMS);
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

        TilbaketrekkingsKvittering tilbaketrekkingsKvittering = (TilbaketrekkingsKvittering)kvitteringBuilder.buildForretningsKvittering(ebmsKvittering);

        assertNotNull(tilbaketrekkingsKvittering.getKonversasjonsId());
        assertNotNull(tilbaketrekkingsKvittering.getTidspunkt());
        assertThat((tilbaketrekkingsKvittering).getBeskrivelse()).isEqualTo("Tilbaketrekking av 'Viktig brev'");
        assertThat((tilbaketrekkingsKvittering).getStatus()).isEqualTo(TilbaketrekkingsStatus.FEILET);
    }

    private EbmsApplikasjonsKvittering createEbmsAapningsKvittering() {
        SDPKvittering kvittering = new SDPKvittering()
                .withAapning(new SDPAapning())
                .withTidspunkt(DateTime.now())
                .withKonversasjonsId(UUID.randomUUID().toString());

        return createEbmsKvittering(kvittering);
    }

    private EbmsApplikasjonsKvittering createEbmsLeveringsKvittering() {
        SDPKvittering kvittering = new SDPKvittering()
                .withLevering(new SDPLevering())
                .withTidspunkt(DateTime.now())
                .withKonversasjonsId(UUID.randomUUID().toString());

        return createEbmsKvittering(kvittering);
    }

    private EbmsApplikasjonsKvittering createEbmsVarslingFeiletKvittering(SDPVarslingskanal varslingskanal) {
        SDPKvittering kvittering = new SDPKvittering()
                .withVarslingfeilet(new SDPVarslingfeilet(varslingskanal, "Varsling feilet 'Viktig brev'"))
                .withTidspunkt(DateTime.now())
                .withKonversasjonsId(UUID.randomUUID().toString());

        return createEbmsKvittering(kvittering);
    }

    private EbmsApplikasjonsKvittering createEbmsTilbaketrekkingsKvittering(SDPTilbaketrekkingsstatus status) {
        SDPKvittering kvittering = new SDPKvittering()
                .withTilbaketrekking(new SDPTilbaketrekkingsresultat(status, "Tilbaketrekking av 'Viktig brev'"))
                .withTidspunkt(DateTime.now())
                .withKonversasjonsId(UUID.randomUUID().toString());

        return createEbmsKvittering(kvittering);
    }

    private EbmsApplikasjonsKvittering createEbmsKvittering(SDPKvittering kvittering) {
        Organisasjonsnummer avsender = new Organisasjonsnummer("123");
        Organisasjonsnummer mottaker = new Organisasjonsnummer("456");

        final StandardBusinessDocument sbd = new StandardBusinessDocument()
                .withStandardBusinessDocumentHeader(new StandardBusinessDocumentHeader())
                .withAny(kvittering);

        return EbmsApplikasjonsKvittering.create(avsender, mottaker, sbd).build();
    }



}
