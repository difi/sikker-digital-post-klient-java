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

import no.difi.begrep.sdp.schema_v10.SDPFeiltype;
import no.difi.begrep.sdp.schema_v10.SDPVarslingskanal;
import no.difi.sdp.client.domain.Feil;
import no.difi.sdp.client.domain.Prioritet;
import no.difi.sdp.client.domain.kvittering.AapningsKvittering;
import no.difi.sdp.client.domain.kvittering.LeveringsKvittering;
import no.difi.sdp.client.domain.kvittering.VarslingFeiletKvittering;
import no.digipost.api.representations.EbmsApplikasjonsKvittering;
import no.digipost.api.representations.EbmsOutgoingMessage;
import no.digipost.api.representations.EbmsPullRequest;
import no.digipost.api.representations.Organisasjonsnummer;
import org.junit.Test;

import static no.difi.sdp.client.ObjectMother.createEbmsAapningsKvittering;
import static no.difi.sdp.client.ObjectMother.createEbmsFeil;
import static no.difi.sdp.client.ObjectMother.createEbmsLeveringsKvittering;
import static no.difi.sdp.client.ObjectMother.createEbmsVarslingFeiletKvittering;
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
        assertThat(varslingFeiletKvittering.getVarslingskanal()).isEqualTo(VarslingFeiletKvittering.Varslingskanal.EPOST);
    }

    @Test
    public void should_build_varsling_feilet_sms_kvittering() {
        EbmsApplikasjonsKvittering ebmsKvittering = createEbmsVarslingFeiletKvittering(SDPVarslingskanal.SMS);

        VarslingFeiletKvittering varslingFeiletKvittering = (VarslingFeiletKvittering) kvitteringBuilder.buildForretningsKvittering(ebmsKvittering);

        assertNotNull(varslingFeiletKvittering.getKonversasjonsId());
        assertNotNull(varslingFeiletKvittering.getTidspunkt());
        assertThat((varslingFeiletKvittering).getBeskrivelse()).isEqualTo("Varsling feilet 'Viktig brev'");
        assertThat((varslingFeiletKvittering).getVarslingskanal()).isEqualTo(VarslingFeiletKvittering.Varslingskanal.SMS);
    }

    @Test
    public void should_build_klient_feil() {
        EbmsApplikasjonsKvittering ebmsKvittering = createEbmsFeil(SDPFeiltype.KLIENT);

        Feil feil = (Feil) kvitteringBuilder.buildForretningsKvittering(ebmsKvittering);

        assertNotNull(feil.getKonversasjonsId());
        assertNotNull(feil.getTidspunkt());
        assertThat(feil.getFeiltype()).isEqualTo(Feil.Feiltype.KLIENT);
        assertThat(feil.getDetaljer()).isEqualTo("Feilinformasjon");
    }

    @Test
    public void should_build_server_feil() {
        EbmsApplikasjonsKvittering ebmsKvittering = createEbmsFeil(SDPFeiltype.SERVER);

        Feil feil = (Feil) kvitteringBuilder.buildForretningsKvittering(ebmsKvittering);

        assertNotNull(feil.getKonversasjonsId());
        assertNotNull(feil.getTidspunkt());
        assertThat(feil.getFeiltype()).isEqualTo(Feil.Feiltype.SERVER);
        assertThat(feil.getDetaljer()).isEqualTo("Feilinformasjon");
    }

}
