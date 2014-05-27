package no.difi.sdp.client.internal;

import no.difi.begrep.*;
import no.difi.begrep.sdp.schema_v10.*;
import no.difi.sdp.client.domain.Avsender;
import no.difi.sdp.client.domain.Dokument;
import no.difi.sdp.client.domain.Forsendelse;
import no.difi.sdp.client.domain.Mottaker;
import no.difi.sdp.client.domain.digital_post.DigitalPost;
import no.difi.sdp.client.domain.digital_post.EpostVarsel;
import no.difi.sdp.client.domain.digital_post.SmsVarsel;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.w3.xmldsig.Reference;
import org.w3.xmldsig.Signature;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class SDPBuilder {

    /**
     * ISO6523-identifikasjon av Brønnøysundregisterets organisasjonsnummer som identifikator.
     */
    private static final String ORGNR_IDENTIFIER = "9908:";

    public SDPManifest createManifest(Avsender avsender, Forsendelse forsendelse) {
        Mottaker mottaker = forsendelse.getDigitalPost().getMottaker();

        DifiPerson difiPerson = new DifiPerson().withPersonidentifikator(mottaker.getPersonidentifikator());
        SDPMottaker sdpMottaker = new SDPMottaker(null, difiPerson);

        String fakturaReferanse = null; // Ikke send fakturareferanse i manifest
        SDPAvsender sdpAvsender = new SDPAvsender(sdpOrganisasjon(avsender), avsender.getAvsenderIdentifikator(), fakturaReferanse);

        String spraakkode = forsendelse.getSpraakkode();
        SDPDokument sdpHovedDokument = sdpDokument(forsendelse.getDokumentpakke().getHoveddokument(), spraakkode);

        List<SDPDokument> sdpVedlegg = new ArrayList<SDPDokument>();
        for (Dokument dokument : forsendelse.getDokumentpakke().getVedlegg()) {
            sdpVedlegg.add(sdpDokument(dokument, spraakkode));
        }

        return new SDPManifest(sdpMottaker, sdpAvsender, sdpHovedDokument, sdpVedlegg);
    }

    public SDPDigitalPost buildDigitalPost(Avsender avsender, Forsendelse forsendelse) {
        SDPAvsender sdpAvsender = sdpAvsender(avsender);
        SDPMottaker sdpMottaker = sdpMottaker(forsendelse.getDigitalPost().getMottaker(), forsendelse);

        SDPDigitalPostInfo sdpDigitalPostInfo = sdpDigitalPostinfo(forsendelse);

        String konversasjonsId = forsendelse.getKonversasjonsId();

        Signature signature = new Signature(); // TODO: Hva skal vi signere og hvordan? Legges denne på fra et av filtrene?
        SDPFysiskPostInfo fysiskPostInfo = null; // TODO: støtte fysisk post
        Reference dokumentpakkefingeravtrykk = new Reference(); // TODO: Generere nøkkel og bygge dokumentpakke
        return new SDPDigitalPost(konversasjonsId, signature, sdpAvsender, sdpMottaker, sdpDigitalPostInfo, fysiskPostInfo, dokumentpakkefingeravtrykk);
    }

    private SDPDokument sdpDokument(Dokument dokument, String spraakkode) {
        SDPTittel sdpTittel = new SDPTittel(dokument.getTittel(), spraakkode);
        return new SDPDokument(sdpTittel, dokument.getFilnavn(), dokument.getMimeType());
    }

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
        String fakturaReferanse = avsender.getFakturaReferanse();
        String identifikator = avsender.getAvsenderIdentifikator();
        SDPOrganisasjon organisasjon = sdpOrganisasjon(avsender);

        return new SDPAvsender(organisasjon, identifikator, fakturaReferanse);
    }

    private SDPOrganisasjon sdpOrganisasjon(Avsender avsender) {
        return new SDPOrganisasjon(ORGNR_IDENTIFIER + avsender.getOrganisasjonsnummer(), SDPIso6523Authority.ISO_6523_ACTORID_UPIS);
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
