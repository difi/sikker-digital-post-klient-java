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

import no.difi.begrep.sdp.schema_v10.SDPAvsender;
import no.difi.begrep.sdp.schema_v10.SDPDigitalPost;
import no.difi.begrep.sdp.schema_v10.SDPDigitalPostInfo;
import no.difi.begrep.sdp.schema_v10.SDPDokument;
import no.difi.begrep.sdp.schema_v10.SDPEpostVarsel;
import no.difi.begrep.sdp.schema_v10.SDPEpostVarselTekst;
import no.difi.begrep.sdp.schema_v10.SDPFysiskPostInfo;
import no.difi.begrep.sdp.schema_v10.SDPIso6523Authority;
import no.difi.begrep.sdp.schema_v10.SDPManifest;
import no.difi.begrep.sdp.schema_v10.SDPMottaker;
import no.difi.begrep.sdp.schema_v10.SDPOrganisasjon;
import no.difi.begrep.sdp.schema_v10.SDPPerson;
import no.difi.begrep.sdp.schema_v10.SDPRepetisjoner;
import no.difi.begrep.sdp.schema_v10.SDPSikkerhetsnivaa;
import no.difi.begrep.sdp.schema_v10.SDPSmsVarsel;
import no.difi.begrep.sdp.schema_v10.SDPSmsVarselTekst;
import no.difi.begrep.sdp.schema_v10.SDPTittel;
import no.difi.begrep.sdp.schema_v10.SDPVarsler;
import no.difi.begrep.sdp.schema_v10.SDPVirksomhet;
import no.difi.sdp.client.domain.Avsender;
import no.difi.sdp.client.domain.Dokument;
import no.difi.sdp.client.domain.Forsendelse;
import no.difi.sdp.client.domain.Mottaker;
import no.difi.sdp.client.domain.digital_post.DigitalPost;
import no.difi.sdp.client.domain.digital_post.EpostVarsel;
import no.difi.sdp.client.domain.digital_post.SmsVarsel;
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

        SDPPerson sdpPerson = new SDPPerson().withPersonidentifikator(mottaker.getPersonidentifikator());
        SDPMottaker sdpMottaker = new SDPMottaker(null, sdpPerson);

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

        // Signatur og fingeravtrykk legges på automatisk av klienten hvis de ikke er satt
        Signature signature = null;
        Reference dokumentpakkefingeravtrykk = null;

        SDPFysiskPostInfo fysiskPostInfo = null; // TODO: støtte fysisk post
        return new SDPDigitalPost(signature, sdpAvsender, sdpMottaker, sdpDigitalPostInfo, fysiskPostInfo, dokumentpakkefingeravtrykk);
    }

    private SDPDokument sdpDokument(Dokument dokument, String spraakkode) {
        SDPTittel sdpTittel = new SDPTittel(dokument.getTittel(), spraakkode);
        return new SDPDokument(sdpTittel, dokument.getFilnavn(), dokument.getMimeType());
    }

    private SDPMottaker sdpMottaker(Mottaker mottaker, Forsendelse forsendelse) {
        // Bygg SDP:Mottaker. SDP:Mottaker er av typen sdp:person som har en del felter som ikke er relevant i denne konteksten.
        // Vi setter felter i henhold til det som er definert for sdp:melding (http://begrep.difi.no/SikkerDigitalPost/utkast/StandardBusinessDocument/Melding/Person)

        SDPVirksomhet virksomhet = null; // Sending til virksomheter er ikke støttet

        SDPPerson sdpPerson = new SDPPerson(mottaker.getPersonidentifikator(), mottaker.getPostkasseadresse());

        return new SDPMottaker(virksomhet, sdpPerson);
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

    private SDPDigitalPostInfo sdpDigitalPostinfo(Forsendelse forsendelse) {
        LocalDate virkningsdato = null;
        DigitalPost digitalPost = forsendelse.getDigitalPost();
        if (digitalPost.getVirkningsdato() != null) {
            virkningsdato = new LocalDate(digitalPost.getVirkningsdato());
        }

        boolean aapningskvittering = digitalPost.isAapningskvittering();
        SDPSikkerhetsnivaa sikkerhetsnivaa = digitalPost.getSikkerhetsnivaa().getXmlValue();
        SDPTittel tittel = new SDPTittel(digitalPost.getIkkeSensitivTittel(), null);
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
        if (smsVarsel != null) {
            SDPSmsVarselTekst smsVarselTekst = new SDPSmsVarselTekst(smsVarsel.getVarslingsTekst(), spraakkode);
            return new SDPSmsVarsel(smsVarsel.getMobilnummer(), smsVarselTekst, new SDPRepetisjoner(smsVarsel.getDagerEtter()));
        }
        return null;
    }

    private SDPEpostVarsel sdpEpostVarsel(EpostVarsel epostVarsel, String spraakkode) {
        if (epostVarsel != null) {
            SDPEpostVarselTekst epostVarselTekst = new SDPEpostVarselTekst(epostVarsel.getVarslingsTekst(), spraakkode);
            return new SDPEpostVarsel(epostVarsel.getEpostadresse(), epostVarselTekst, new SDPRepetisjoner(epostVarsel.getDagerEtter()));
        }
        return null;
    }

}
