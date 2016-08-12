package no.difi.sdp.client2.domain;

import no.digipost.api.representations.Organisasjonsnummer;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class AktørOrganisasjonsnummerTest {
    @Test
    public void create_organisasjonsnummer_from_string() {
        String orgnr = "984661185";

        AktørOrganisasjonsnummer organisasjonsnummer = AktørOrganisasjonsnummer.of(orgnr);

        assertThat(organisasjonsnummer.getOrganisasjonsnummer(), equalTo(orgnr));
    }

    @Test
    public void create_organisasjonsnummer_from_class()  {
        Organisasjonsnummer orgnr = Organisasjonsnummer.of("984661185");

        AktørOrganisasjonsnummer organisasjonsnummer = AktørOrganisasjonsnummer.of(orgnr);

        assertThat(organisasjonsnummer.getOrganisasjonsnummer(), equalTo(orgnr.getOrganisasjonsnummer()));
    }

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