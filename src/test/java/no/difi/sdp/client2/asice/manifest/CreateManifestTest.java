package no.difi.sdp.client2.asice.manifest;

import no.difi.sdp.client2.ObjectMother;
import no.difi.sdp.client2.domain.Behandlingsansvarlig;
import no.difi.sdp.client2.domain.Forsendelse;
import no.difi.sdp.client2.domain.Mottaker;
import no.difi.sdp.client2.domain.digital_post.DigitalPost;
import no.difi.sdp.client2.domain.exceptions.XmlValideringException;
import org.junit.Before;
import org.junit.Test;

import static no.difi.sdp.client2.ObjectMother.mottakerSertifikat;

public class CreateManifestTest {

    private CreateManifest sut;

    @Before
    public void setUp() throws Exception {
        sut = new CreateManifest();
    }

    @Test
    public void accept_valid_forsendelse() {
        Forsendelse forsendelse = ObjectMother.forsendelse();

        sut.createManifest(forsendelse); // No Exceptions
    }

    @Test(expected = XmlValideringException.class)
    public void should_validate_manifest() {
        Mottaker mottaker = Mottaker.builder("04036125433", null, mottakerSertifikat(), "984661185").build();
        Behandlingsansvarlig behandlingsasnvarlig = Behandlingsansvarlig.builder("991825827").build();
        Forsendelse ugyldigForsendelse = Forsendelse.digital(behandlingsasnvarlig, DigitalPost.builder(mottaker, "tittel").build(), ObjectMother.dokumentpakke()).build();

        sut.createManifest(ugyldigForsendelse);
    }

}