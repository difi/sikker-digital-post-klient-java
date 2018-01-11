package no.difi.sdp.client2.domain.utvidelser;

import no.difi.begrep.sdp.utvidelser.lenke.SDPLenke;
import no.difi.sdp.client2.domain.exceptions.XmlValideringException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class LenkeTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void lenke_med_bare_obligatoriske_felter_satt_oversettes_riktig() {
        SDPLenke lenke = Lenke.builder("lenke.xml", "https://www.avsender.no").build().jaxbObject();
        assertThat(lenke.getUrl(), is("https://www.avsender.no"));
        assertThat(lenke.getBeskrivelse(), nullValue());
        assertThat(lenke.getKnappTekst(), nullValue());
        assertThat(lenke.getFrist(), nullValue());
    }

    @Test
    public void lenke_med_alle_felter_satt_oversettes_riktig() {
        SDPLenke lenke = Lenke.builder("lenke.xml", "https://www.avsender.no")
                .beskrivelse("Beskrivelse")
                .knappTekst("Knappe-tekst")
                .frist(ZonedDateTime.of(LocalDateTime.of(2017, 1, 10, 11, 29), ZoneId.of("Europe/Oslo")))
                .build()
                .jaxbObject();
        assertThat(lenke.getUrl(), is("https://www.avsender.no"));
        assertThat(lenke.getBeskrivelse().getValue(), is("Beskrivelse"));
        assertThat(lenke.getKnappTekst().getValue(), is("Knappe-tekst"));
        assertThat(lenke.getFrist(), is(ZonedDateTime.of(LocalDateTime.of(2017, 1, 10, 11, 29), ZoneId.of("Europe/Oslo"))));
    }

    @Test
    public void kaster_exception_ved_valideringsfeil() {
        Lenke lenkeMedUgyldigURL = Lenke.builder("lenke.xml", "ugyldig-url-som-mangler-protokoll.no").build();
        thrown.expect(XmlValideringException.class);
        lenkeMedUgyldigURL.getBytes();
    }

}