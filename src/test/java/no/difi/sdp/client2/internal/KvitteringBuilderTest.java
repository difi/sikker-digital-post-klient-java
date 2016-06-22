package no.difi.sdp.client2.internal;

import no.difi.begrep.sdp.schema_v10.SDPFeiltype;
import no.difi.begrep.sdp.schema_v10.SDPVarslingskanal;
import no.difi.sdp.client2.ObjectMother;
import no.difi.sdp.client2.domain.Prioritet;
import no.difi.sdp.client2.domain.kvittering.*;
import no.digipost.api.representations.EbmsApplikasjonsKvittering;
import no.digipost.api.representations.EbmsOutgoingMessage;
import no.digipost.api.representations.EbmsPullRequest;
import no.digipost.api.representations.Organisasjonsnummer;
import org.junit.Test;

import static no.difi.sdp.client2.ObjectMother.*;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

public class KvitteringBuilderTest {

    private final KvitteringBuilder kvitteringBuilder = new KvitteringBuilder();

    @Test
    public void should_build_pull_request_with_standard_priority_and_mpc() {
        KvitteringForespoersel kvitteringForespoersel = KvitteringForespoersel.builder(Prioritet.NORMAL).mpcId("mpcId").build();
        Organisasjonsnummer organisasjonsnummer = Organisasjonsnummer.of("988015814");
        EbmsPullRequest ebmsPullRequest = kvitteringBuilder.buildEbmsPullRequest(organisasjonsnummer, kvitteringForespoersel);

        assertThat(ebmsPullRequest.getEbmsMottaker().orgnr).isEqualTo(organisasjonsnummer);
        assertThat(ebmsPullRequest.prioritet).isEqualTo(EbmsOutgoingMessage.Prioritet.NORMAL);
        assertThat(ebmsPullRequest.mpcId).isEqualTo("mpcId");
    }

    @Test
    public void should_build_pull_request_with_standard_high_priority_and_null_mpc() {
        KvitteringForespoersel kvitteringForespoersel = KvitteringForespoersel.builder(Prioritet.PRIORITERT).build();
        Organisasjonsnummer organisasjonsnummer = Organisasjonsnummer.of("988015814");
        EbmsPullRequest ebmsPullRequest = kvitteringBuilder.buildEbmsPullRequest(organisasjonsnummer, kvitteringForespoersel);

        assertThat(ebmsPullRequest.getEbmsMottaker().orgnr).isEqualTo(organisasjonsnummer);
        assertThat(ebmsPullRequest.prioritet).isEqualTo(EbmsOutgoingMessage.Prioritet.PRIORITERT);
        assertThat(ebmsPullRequest.mpcId).isNull();
    }

    @Test
    public void should_build_aapnings_kvittering() {
        EbmsApplikasjonsKvittering ebmsKvittering = createEbmsAapningsKvittering();

        AapningsKvittering aapningKvittering = (AapningsKvittering) kvitteringBuilder.buildForretningsKvittering(ebmsKvittering);

        assertNotNull(aapningKvittering.kvitteringsInfo.getKonversasjonsId());
        assertNotNull(aapningKvittering.kvitteringsInfo.getTidspunkt());
    }

    @Test
    public void builds_leverings_kvittering() {
        EbmsApplikasjonsKvittering ebmsKvittering = createEbmsLeveringsKvittering();

        LeveringsKvittering leveringsKvittering = (LeveringsKvittering) kvitteringBuilder.buildForretningsKvittering(ebmsKvittering);

        assertNotNull(leveringsKvittering.kvitteringsInfo.getKonversasjonsId());
        assertNotNull(leveringsKvittering.kvitteringsInfo.getTidspunkt());
        assertNotNull(leveringsKvittering.kvitteringsInfo.getReferanseTilMeldingId());

        assertNotNull(leveringsKvittering.kanBekreftesSomBehandletKvittering.getMeldingsId());
        assertNotNull(leveringsKvittering.kanBekreftesSomBehandletKvittering.getReferanseTilMeldingSomKvitteres());
    }

    @Test
    public void builds_mottaks_kvittering() {
        EbmsApplikasjonsKvittering ebmsKvittering = ObjectMother.createEbmsMottaksKvittering();

        MottaksKvittering mottaksKvittering = (MottaksKvittering) kvitteringBuilder.buildForretningsKvittering(ebmsKvittering);

        assertNotNull(mottaksKvittering.kvitteringsInfo.getKonversasjonsId());
        assertNotNull(mottaksKvittering.kvitteringsInfo.getTidspunkt());
        assertNotNull(mottaksKvittering.kvitteringsInfo.getReferanseTilMeldingId());

        assertNotNull(mottaksKvittering.kanBekreftesSomBehandletKvittering.getMeldingsId());
        assertNotNull(mottaksKvittering.kanBekreftesSomBehandletKvittering.getReferanseTilMeldingSomKvitteres());
    }

    @Test
    public void builds_returpost_kvittering() {
        EbmsApplikasjonsKvittering ebmsKvittering = ObjectMother.createEbmsReturpostKvittering();

        ReturpostKvittering returpostKvittering = (ReturpostKvittering) kvitteringBuilder.buildForretningsKvittering(ebmsKvittering);

        assertNotNull(returpostKvittering.kvitteringsInfo.getKonversasjonsId());
        assertNotNull(returpostKvittering.kvitteringsInfo.getTidspunkt());
        assertNotNull(returpostKvittering.kvitteringsInfo.getReferanseTilMeldingId());

        assertNotNull(returpostKvittering.kanBekreftesSomBehandletKvittering.getMeldingsId());
        assertNotNull(returpostKvittering.kanBekreftesSomBehandletKvittering.getReferanseTilMeldingSomKvitteres());
    }

    @Test
    public void builds_varsling_feilet_epost_kvittering() {
        EbmsApplikasjonsKvittering ebmsKvittering = createEbmsVarslingFeiletKvittering(SDPVarslingskanal.EPOST);

        VarslingFeiletKvittering varslingFeiletKvittering = (VarslingFeiletKvittering) kvitteringBuilder.buildForretningsKvittering(ebmsKvittering);

        assertNotNull(varslingFeiletKvittering.kvitteringsInfo.getKonversasjonsId());
        assertNotNull(varslingFeiletKvittering.kvitteringsInfo.getTidspunkt());
        assertNotNull(varslingFeiletKvittering.kvitteringsInfo.getReferanseTilMeldingId());

        assertNotNull(varslingFeiletKvittering.kanBekreftesSomBehandletKvittering.getMeldingsId());
        assertNotNull(varslingFeiletKvittering.kanBekreftesSomBehandletKvittering.getReferanseTilMeldingSomKvitteres());


        assertThat(varslingFeiletKvittering.getBeskrivelse()).isEqualTo("Varsling feilet 'Viktig brev'");
        assertThat(varslingFeiletKvittering.getVarslingskanal()).isEqualTo(VarslingFeiletKvittering.Varslingskanal.EPOST);
    }


    @Test
    public void builds_varsling_feilet_sms_kvittering() {
        EbmsApplikasjonsKvittering ebmsKvittering = createEbmsVarslingFeiletKvittering(SDPVarslingskanal.SMS);

        VarslingFeiletKvittering varslingFeiletKvittering = (VarslingFeiletKvittering) kvitteringBuilder.buildForretningsKvittering(ebmsKvittering);

        assertNotNull(varslingFeiletKvittering.kvitteringsInfo.getKonversasjonsId());
        assertNotNull(varslingFeiletKvittering.kvitteringsInfo.getTidspunkt());
        assertNotNull(varslingFeiletKvittering.kvitteringsInfo.getReferanseTilMeldingId());

        assertNotNull(varslingFeiletKvittering.kanBekreftesSomBehandletKvittering.getMeldingsId());
        assertNotNull(varslingFeiletKvittering.kanBekreftesSomBehandletKvittering.getReferanseTilMeldingSomKvitteres());


        assertThat((varslingFeiletKvittering).getBeskrivelse()).isEqualTo("Varsling feilet 'Viktig brev'");
        assertThat((varslingFeiletKvittering).getVarslingskanal()).isEqualTo(VarslingFeiletKvittering.Varslingskanal.SMS);
    }

    @Test
    public void builds_klient_feil() {
        EbmsApplikasjonsKvittering ebmsKvittering = createEbmsFeil(SDPFeiltype.KLIENT);

        Feil feil = (Feil) kvitteringBuilder.buildForretningsKvittering(ebmsKvittering);

        assertNotNull(feil.kvitteringsInfo.getKonversasjonsId());
        assertNotNull(feil.kvitteringsInfo.getTidspunkt());
        assertNotNull(feil.kvitteringsInfo.getReferanseTilMeldingId());

        assertNotNull(feil.kanBekreftesSomBehandletKvittering.getMeldingsId());
        assertNotNull(feil.kanBekreftesSomBehandletKvittering.getReferanseTilMeldingSomKvitteres());

        assertThat(feil.getFeiltype()).isEqualTo(Feil.Feiltype.KLIENT);
        assertThat(feil.getDetaljer()).isEqualTo("Feilinformasjon");
    }

    @Test
    public void builds_server_feil() {
        EbmsApplikasjonsKvittering ebmsKvittering = createEbmsFeil(SDPFeiltype.SERVER);

        Feil feil = (Feil) kvitteringBuilder.buildForretningsKvittering(ebmsKvittering);

        assertNotNull(feil.kvitteringsInfo.getKonversasjonsId());
        assertNotNull(feil.kvitteringsInfo.getTidspunkt());
        assertNotNull(feil.kvitteringsInfo.getReferanseTilMeldingId());

        assertNotNull(feil.kanBekreftesSomBehandletKvittering.getMeldingsId());
        assertNotNull(feil.kanBekreftesSomBehandletKvittering.getReferanseTilMeldingSomKvitteres());


        assertThat(feil.getFeiltype()).isEqualTo(Feil.Feiltype.SERVER);
        assertThat(feil.getDetaljer()).isEqualTo("Feilinformasjon");
    }

}
