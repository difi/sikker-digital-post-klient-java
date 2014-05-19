package no.difi.sdp.client.internal;

import no.difi.begrep.sdp.schema_v10.SDPDigitalPost;
import no.difi.sdp.client.domain.Avsender;
import no.difi.sdp.client.domain.Forsendelse;
import no.difi.sdp.client.domain.Mottaker;
import no.posten.dpost.offentlig.api.representations.EbmsForsendelse;
import no.posten.dpost.offentlig.api.representations.Organisasjonsnummer;

public class EbmsForsendelseBuilder {

    private final SDPBuilder sdpBuilder;

    public EbmsForsendelseBuilder() {
        sdpBuilder = new SDPBuilder();
    }

    public EbmsForsendelse buildEbmsForsendelse(Avsender avsender, Forsendelse forsendelse) {
        Mottaker mottaker = forsendelse.getDigitalPost().getMottaker();
        
        Organisasjonsnummer avsenderOrganisasjonsnummer = new Organisasjonsnummer(avsender.getOrganisasjonsnummer());
        Organisasjonsnummer mottakerOrganisasjonsnummer = new Organisasjonsnummer(mottaker.getOrganisasjonsnummerPostkasse());
        // TODO: Gjør ferdig denne
        //EbmsForsendelse.create(avsenderOrganisasjonsnummer, mottakerOrganisasjonsnummer, createSikkerDigitalPost(avsender, forsendelse));
        return null;
    }

    private SDPDigitalPost createSikkerDigitalPost(Avsender avsender, Forsendelse forsendelse) {
        return sdpBuilder.buildDigitalPost(avsender, forsendelse);
    }
}
