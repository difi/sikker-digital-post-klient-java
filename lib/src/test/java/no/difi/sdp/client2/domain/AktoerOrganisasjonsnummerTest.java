package no.difi.sdp.client2.domain;

import no.digipost.api.representations.Organisasjonsnummer;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class AktoerOrganisasjonsnummerTest {
    @Test
    public void create_organisasjonsnummer_from_string() {
        String orgnr = "984661185";

        AktoerOrganisasjonsnummer organisasjonsnummer = AktoerOrganisasjonsnummer.of(orgnr);

        assertThat(organisasjonsnummer.getOrganisasjonsnummer(), equalTo(orgnr));
    }

    @Test
    public void create_organisasjonsnummer_from_class() {
        Organisasjonsnummer orgnr = Organisasjonsnummer.of("984661185");

        AktoerOrganisasjonsnummer organisasjonsnummer = AktoerOrganisasjonsnummer.of(orgnr);

        assertThat(organisasjonsnummer.getOrganisasjonsnummer(), equalTo(orgnr.getOrganisasjonsnummer()));
    }

    @Test
    public void forfrem_til_avsender() {
        AktoerOrganisasjonsnummer organisasjonsnummer = AktoerOrganisasjonsnummer.of("984661185");

        AvsenderOrganisasjonsnummer avsenderOrganisasjonsnummer = organisasjonsnummer.forfremTilAvsender();

        assertThat(avsenderOrganisasjonsnummer.getOrganisasjonsnummerMedLandkode(), equalTo(organisasjonsnummer.getOrganisasjonsnummerMedLandkode()));
    }

    @Test
    public void forfrem_til_databehandler() {
        AktoerOrganisasjonsnummer organisasjonsnummer = AktoerOrganisasjonsnummer.of("984661185");

        DatabehandlerOrganisasjonsnummer databehandlerOrganisasjonsnummer = organisasjonsnummer.forfremTilDatabehandler();

        assertThat(databehandlerOrganisasjonsnummer.getOrganisasjonsnummerMedLandkode(), equalTo(organisasjonsnummer.getOrganisasjonsnummerMedLandkode()));
    }
}