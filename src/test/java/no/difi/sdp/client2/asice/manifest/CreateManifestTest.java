package no.difi.sdp.client2.asice.manifest;

import no.difi.sdp.client2.ObjectMother;
import no.difi.sdp.client2.domain.Avsender;
import no.difi.sdp.client2.domain.Forsendelse;
import no.difi.sdp.client2.domain.Mottaker;
import no.difi.sdp.client2.domain.digital_post.DigitalPost;
import no.difi.sdp.client2.domain.exceptions.XmlValideringException;
import no.digipost.api.representations.Organisasjonsnummer;
import org.junit.Before;
import org.junit.Test;

import static no.difi.sdp.client2.ObjectMother.mottakerSertifikat;

public class CreateManifestTest {

    private CreateManifest sut;

    @Before
    public void set_up() throws Exception {
        sut = new CreateManifest();
    }

    @Test
    public void accept_valid_forsendelse() {
        Forsendelse forsendelse = ObjectMother.forsendelse();

        sut.createManifest(forsendelse); // No Exceptions
    }

    @Test(expected = XmlValideringException.class)
    public void should_validate_manifest() {
        Mottaker mottaker = Mottaker.builder("04036125433", null, mottakerSertifikat(), Organisasjonsnummer.of("984661185")).build();
        Avsender avsender = Avsender.builder(ObjectMother.avsenderOrganisasjonsnummer()).build();
        Forsendelse ugyldigForsendelse = Forsendelse.digital(avsender, DigitalPost.builder(mottaker, "tittel").build(), ObjectMother.dokumentpakke()).build();

        sut.createManifest(ugyldigForsendelse);
    }

}