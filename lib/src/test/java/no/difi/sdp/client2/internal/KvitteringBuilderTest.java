/*
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
package no.difi.sdp.client2.internal;

import no.difi.begrep.sdp.schema_v10.SDPFeiltype;
import no.difi.begrep.sdp.schema_v10.SDPVarslingskanal;
import no.difi.sdp.client2.ObjectMother;
import no.difi.sdp.client2.domain.Prioritet;
import no.difi.sdp.client2.domain.kvittering.AapningsKvittering;
import no.difi.sdp.client2.domain.kvittering.Feil;
import no.difi.sdp.client2.domain.kvittering.KvitteringForespoersel;
import no.difi.sdp.client2.domain.kvittering.LeveringsKvittering;
import no.difi.sdp.client2.domain.kvittering.MottaksKvittering;
import no.difi.sdp.client2.domain.kvittering.ReturpostKvittering;
import no.difi.sdp.client2.domain.kvittering.VarslingFeiletKvittering;
import no.digipost.api.representations.EbmsApplikasjonsKvittering;
import no.digipost.api.representations.EbmsOutgoingMessage;
import no.digipost.api.representations.EbmsPullRequest;
import no.digipost.api.representations.Organisasjonsnummer;
import org.junit.jupiter.api.Test;

import static no.difi.sdp.client2.ObjectMother.createEbmsAapningsKvittering;
import static no.difi.sdp.client2.ObjectMother.createEbmsFeil;
import static no.difi.sdp.client2.ObjectMother.createEbmsLeveringsKvittering;
import static no.difi.sdp.client2.ObjectMother.createEbmsVarslingFeiletKvittering;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class KvitteringBuilderTest {

    private final KvitteringBuilder kvitteringBuilder = new KvitteringBuilder();

    @Test
    public void should_build_pull_request_with_standard_priority_and_mpc() {
        KvitteringForespoersel kvitteringForespoersel = KvitteringForespoersel.builder(Prioritet.NORMAL).mpcId("mpcId").build();
        Organisasjonsnummer organisasjonsnummer = Organisasjonsnummer.of("988015814");
        EbmsPullRequest ebmsPullRequest = kvitteringBuilder.buildEbmsPullRequest(organisasjonsnummer, kvitteringForespoersel);

        assertThat(ebmsPullRequest.getEbmsMottaker().orgnr, equalTo(organisasjonsnummer));
        assertThat(ebmsPullRequest.prioritet, equalTo(EbmsOutgoingMessage.Prioritet.NORMAL));
        assertThat(ebmsPullRequest.mpcId, equalTo("mpcId"));
    }

    @Test
    public void should_build_pull_request_with_standard_high_priority_and_null_mpc() {
        KvitteringForespoersel kvitteringForespoersel = KvitteringForespoersel.builder(Prioritet.PRIORITERT).build();
        Organisasjonsnummer organisasjonsnummer = Organisasjonsnummer.of("988015814");
        EbmsPullRequest ebmsPullRequest = kvitteringBuilder.buildEbmsPullRequest(organisasjonsnummer, kvitteringForespoersel);

        assertThat(ebmsPullRequest.getEbmsMottaker().orgnr, equalTo(organisasjonsnummer));
        assertThat(ebmsPullRequest.prioritet, equalTo(EbmsOutgoingMessage.Prioritet.PRIORITERT));
        assertThat(ebmsPullRequest.mpcId, is(nullValue()));
    }

    @Test
    public void should_build_aapnings_kvittering() {
        EbmsApplikasjonsKvittering ebmsKvittering = createEbmsAapningsKvittering();

        AapningsKvittering aapningKvittering = (AapningsKvittering) kvitteringBuilder.buildForretningsKvittering(ebmsKvittering);

        assertNotNull(aapningKvittering.getKonversasjonsId());
        assertNotNull(aapningKvittering.getTidspunkt());
    }

    @Test
    public void builds_leverings_kvittering() {
        EbmsApplikasjonsKvittering ebmsKvittering = createEbmsLeveringsKvittering();

        LeveringsKvittering leveringsKvittering = (LeveringsKvittering) kvitteringBuilder.buildForretningsKvittering(ebmsKvittering);

        assertNotNull(leveringsKvittering.getKonversasjonsId());
        assertNotNull(leveringsKvittering.getTidspunkt());
        assertNotNull(leveringsKvittering.getReferanseTilMeldingId());

        assertNotNull(leveringsKvittering.getMeldingsId());
        assertNotNull(leveringsKvittering.getReferanseTilMeldingSomKvitteres());
    }

    @Test
    public void builds_mottaks_kvittering() {
        EbmsApplikasjonsKvittering ebmsKvittering = ObjectMother.createEbmsMottaksKvittering();

        MottaksKvittering mottaksKvittering = (MottaksKvittering) kvitteringBuilder.buildForretningsKvittering(ebmsKvittering);

        assertNotNull(mottaksKvittering.getKonversasjonsId());
        assertNotNull(mottaksKvittering.getTidspunkt());
        assertNotNull(mottaksKvittering.getReferanseTilMeldingId());

        assertNotNull(mottaksKvittering.getMeldingsId());
        assertNotNull(mottaksKvittering.getReferanseTilMeldingSomKvitteres());
    }

    @Test
    public void builds_returpost_kvittering() {
        EbmsApplikasjonsKvittering ebmsKvittering = ObjectMother.createEbmsReturpostKvittering();

        ReturpostKvittering returpostKvittering = (ReturpostKvittering) kvitteringBuilder.buildForretningsKvittering(ebmsKvittering);

        assertNotNull(returpostKvittering.getKonversasjonsId());
        assertNotNull(returpostKvittering.getTidspunkt());
        assertNotNull(returpostKvittering.getReferanseTilMeldingId());

        assertNotNull(returpostKvittering.getMeldingsId());
        assertNotNull(returpostKvittering.getReferanseTilMeldingSomKvitteres());
    }

    @Test
    public void builds_varsling_feilet_epost_kvittering() {
        EbmsApplikasjonsKvittering ebmsKvittering = createEbmsVarslingFeiletKvittering(SDPVarslingskanal.EPOST);

        VarslingFeiletKvittering varslingFeiletKvittering = (VarslingFeiletKvittering) kvitteringBuilder.buildForretningsKvittering(ebmsKvittering);

        assertNotNull(varslingFeiletKvittering.getKonversasjonsId());
        assertNotNull(varslingFeiletKvittering.getTidspunkt());
        assertNotNull(varslingFeiletKvittering.getReferanseTilMeldingId());

        assertNotNull(varslingFeiletKvittering.getMeldingsId());
        assertNotNull(varslingFeiletKvittering.getReferanseTilMeldingSomKvitteres());


        assertThat(varslingFeiletKvittering.getBeskrivelse(), equalTo("Varsling feilet 'Viktig brev'"));
        assertThat(varslingFeiletKvittering.getVarslingskanal(), equalTo(VarslingFeiletKvittering.Varslingskanal.EPOST));
    }


    @Test
    public void builds_varsling_feilet_sms_kvittering() {
        EbmsApplikasjonsKvittering ebmsKvittering = createEbmsVarslingFeiletKvittering(SDPVarslingskanal.SMS);

        VarslingFeiletKvittering varslingFeiletKvittering = (VarslingFeiletKvittering) kvitteringBuilder.buildForretningsKvittering(ebmsKvittering);

        assertNotNull(varslingFeiletKvittering.getKonversasjonsId());
        assertNotNull(varslingFeiletKvittering.getTidspunkt());
        assertNotNull(varslingFeiletKvittering.getReferanseTilMeldingId());

        assertNotNull(varslingFeiletKvittering.getMeldingsId());
        assertNotNull(varslingFeiletKvittering.getReferanseTilMeldingSomKvitteres());


        assertThat((varslingFeiletKvittering).getBeskrivelse(), equalTo("Varsling feilet 'Viktig brev'"));
        assertThat((varslingFeiletKvittering).getVarslingskanal(), equalTo(VarslingFeiletKvittering.Varslingskanal.SMS));
    }

    @Test
    public void builds_klient_feil() {
        EbmsApplikasjonsKvittering ebmsKvittering = createEbmsFeil(SDPFeiltype.KLIENT);

        Feil feil = (Feil) kvitteringBuilder.buildForretningsKvittering(ebmsKvittering);

        assertNotNull(feil.getKonversasjonsId());
        assertNotNull(feil.getTidspunkt());
        assertNotNull(feil.getReferanseTilMeldingId());

        assertNotNull(feil.getMeldingsId());
        assertNotNull(feil.getReferanseTilMeldingSomKvitteres());

        assertThat(feil.getFeiltype(), equalTo(Feil.Feiltype.KLIENT));
        assertThat(feil.getDetaljer(), equalTo("Feilinformasjon"));
    }

    @Test
    public void builds_server_feil() {
        EbmsApplikasjonsKvittering ebmsKvittering = createEbmsFeil(SDPFeiltype.SERVER);

        Feil feil = (Feil) kvitteringBuilder.buildForretningsKvittering(ebmsKvittering);

        assertNotNull(feil.getKonversasjonsId());
        assertNotNull(feil.getTidspunkt());
        assertNotNull(feil.getReferanseTilMeldingId());

        assertNotNull(feil.getMeldingsId());
        assertNotNull(feil.getReferanseTilMeldingSomKvitteres());


        assertThat(feil.getFeiltype(), equalTo(Feil.Feiltype.SERVER));
        assertThat(feil.getDetaljer(), equalTo("Feilinformasjon"));
    }

}
