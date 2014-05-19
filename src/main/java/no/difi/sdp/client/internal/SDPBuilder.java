package no.difi.sdp.client.internal;

import no.difi.begrep.*;
import no.difi.begrep.sdp.schema_v10.*;
import no.difi.sdp.client.domain.Avsender;
import no.difi.sdp.client.domain.Forsendelse;
import no.difi.sdp.client.domain.Mottaker;
import no.difi.sdp.client.domain.digital_post.DigitalPost;
import no.difi.sdp.client.domain.digital_post.EpostVarsel;
import no.difi.sdp.client.domain.digital_post.SmsVarsel;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.w3.xmldsig.Signature;

public class SDPBuilder {

    /**
     * ISO6523-identifikasjon av Brønnøysundregisterets organisasjonsnummer som identifikator.
     */
    private static final String ORGNR_IDENTIFIER = "9908:";

    public SDPDigitalPost buildDigitalPost(Avsender avsender, Forsendelse forsendelse) {
        String konversasjonsId = forsendelse.getKonversasjonsId();
        SDPAvsender sdpAvsender = sdpAvsender(avsender);
        SDPMottaker sdpMottaker = sdpMottaker(forsendelse.getDigitalPost().getMottaker(), forsendelse);

        SDPDigitalPostInfo sdpDigitalPostInfo = sdpDigitalPostinfo(forsendelse);

        Signature signature = new Signature(); // TODO: Hva skal vi signere og hvordan? Legges denne på fra et av filtrene?
        SDPFysiskPostInfo fysiskPostInfo = null; // TODO: støtte fysisk post
        SDPDokumentpakke dokumentpakke = new SDPDokumentpakke(); // TODO: Generere nøkkel og bygge dokumentpakke

        return new SDPDigitalPost(konversasjonsId, signature, sdpAvsender, sdpMottaker, sdpDigitalPostInfo, fysiskPostInfo, dokumentpakke);
    }

    @SuppressWarnings("ConstantConditions")
    private SDPMottaker sdpMottaker(Mottaker mottaker, Forsendelse forsendelse) {
        // Bygg SDP:Mottaker. SDP:Mottaker er av typen difi:person som har en del felter som ikke er relevant i denne konteksten.
        // Vi setter felter i henhold til det som er definert for sdp:melding (http://begrep.difi.no/SikkerDigitalPost/utkast/StandardBusinessDocument/Melding/Person)
        SDPVirksomhet virksomhet = null; // Sending til virksomheter er ikke støttet
        DifiReservasjon reservasjon = null;
        DifiStatus difiStatus = null;
        String beskrivelse = null;
        String mottakerSertifikat = null;

        DifiKontaktinformasjon kontaktinformasjon = kontaktinformasjon(forsendelse);
        DifiSikkerDigitalPostAdresse digitalPostAdresse = new DifiSikkerDigitalPostAdresse(mottaker.getPostkasseadresse(), mottaker.getOrganisasjonsnummerPostkasse());

        return new SDPMottaker(virksomhet, new DifiPerson(mottaker.getPersonidentifikator(), reservasjon, difiStatus, beskrivelse, kontaktinformasjon, digitalPostAdresse, mottakerSertifikat));
    }

    private SDPAvsender sdpAvsender(Avsender avsender) {
        SDPAvsender sdpAvsender = new SDPAvsender();
        if (avsender.getFakturaReferanse() != null) {
            sdpAvsender.setFakturaReferanse(avsender.getFakturaReferanse());
        }

        return sdpAvsender
                .withAvsenderidentifikator(avsender.getAvsenderIdentifikator())
                .withOrganisasjon(new SDPOrganisasjon().withValue(ORGNR_IDENTIFIER + avsender.getOrganisasjonsnummer()));
    }

    private DifiKontaktinformasjon kontaktinformasjon(Forsendelse forsendelse) {
        DateTime sistOppdatert = null;
        DateTime sistVerifisert = null;

        String mobilnummer = forsendelse.getDigitalPost().getSmsVarsel().getMobilnummer();
        DifiMobiltelefonnummer difiMobiltelefonnummer = new DifiMobiltelefonnummer(mobilnummer, sistOppdatert, sistVerifisert);

        String epostadresse = forsendelse.getDigitalPost().getEpostVarsel().getEpostadresse();
        DifiEpostadresse difiEpostadresse = new DifiEpostadresse(epostadresse, sistOppdatert, sistVerifisert);

        return new DifiKontaktinformasjon(difiMobiltelefonnummer, difiEpostadresse);
    }

    private SDPDigitalPostInfo sdpDigitalPostinfo(Forsendelse forsendelse) {
        LocalDate virkningsdato = null;
        DigitalPost digitalPost = forsendelse.getDigitalPost();
        if (digitalPost.getVirkningsdato() != null) {
            virkningsdato = new LocalDate(digitalPost.getVirkningsdato());
        }

        boolean aapningskvittering = digitalPost.isAapningskvittering();
        SDPSikkerhetsnivaa sikkerhetsnivaa = digitalPost.getSikkerhetsnivaa().getXmlValue();
        SDPTittel tittel = new SDPTittel(digitalPost.getTittel(), null);
        SDPVarsler varsler = sdpVarsler(forsendelse);
        return new SDPDigitalPostInfo(virkningsdato, aapningskvittering, sikkerhetsnivaa, tittel, varsler);
    }

    private SDPVarsler sdpVarsler(Forsendelse forsendelse) {
        String spraakkode = forsendelse.getSpraakkode();

        SDPEpostVarsel epostVarsel = sdpEpostVarsel(forsendelse.getDigitalPost().getEpostVarsel(), spraakkode);
        SDPSmsVarsel smsVarsel = sdpSmsVarsel(forsendelse.getDigitalPost().getSmsVarsel(), spraakkode);

        return new SDPVarsler(epostVarsel, smsVarsel);
    }

    private SDPSmsVarsel sdpSmsVarsel(SmsVarsel smsVarsel, String spraakkode) {
        SDPSmsVarselTekst smsVarselTekst = new SDPSmsVarselTekst(smsVarsel.getTekst(), spraakkode);
        return new SDPSmsVarsel(smsVarselTekst, new SDPRepetisjoner(smsVarsel.getDagerEtter()));
    }

    private SDPEpostVarsel sdpEpostVarsel(EpostVarsel epostVarsel, String spraakkode) {
        SDPEpostVarselTekst epostVarselTekst = new SDPEpostVarselTekst(epostVarsel.getTekst(), spraakkode);
        return new SDPEpostVarsel(epostVarselTekst, new SDPRepetisjoner(epostVarsel.getDagerEtter()));
    }
}
