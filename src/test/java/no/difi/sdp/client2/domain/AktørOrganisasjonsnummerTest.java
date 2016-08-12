package no.difi.sdp.client2.domain;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class AktørOrganisasjonsnummerTest {

    @Test
    public void forfrem_til_avsender() {
        AktørOrganisasjonsnummer organisasjonsnummer = AktørOrganisasjonsnummer.of("984661185");

        AvsenderOrganisasjonsnummer avsenderOrganisasjonsnummer = organisasjonsnummer.forfremTilAvsender();

        assertThat(avsenderOrganisasjonsnummer.getOrganisasjonsnummerMedLandkode(), equalTo(organisasjonsnummer.getOrganisasjonsnummerMedLandkode()));
    }

    @Test
    public void forfrem_til_databehandler() {
        AktørOrganisasjonsnummer organisasjonsnummer = AktørOrganisasjonsnummer.of("984661185");

        DatabehandlerOrganisasjonsnummer databehandlerOrganisasjonsnummer = organisasjonsnummer.forfremTilDatabehandler();

        assertThat(databehandlerOrganisasjonsnummer.getOrganisasjonsnummerMedLandkode(), equalTo(organisasjonsnummer.getOrganisasjonsnummerMedLandkode()));
    }
}