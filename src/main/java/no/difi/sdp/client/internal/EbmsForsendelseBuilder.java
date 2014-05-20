package no.difi.sdp.client.internal;

import no.difi.begrep.sdp.schema_v10.SDPDigitalPost;
import no.difi.sdp.client.asice.AsicEGenerator;
import no.difi.sdp.client.domain.Avsender;
import no.difi.sdp.client.domain.Forsendelse;
import no.difi.sdp.client.domain.Mottaker;
import no.posten.dpost.offentlig.api.representations.EbmsForsendelse;
import no.posten.dpost.offentlig.api.representations.Organisasjonsnummer;

import java.io.InputStream;

public class EbmsForsendelseBuilder {

    private final SDPBuilder sdpBuilder;
    private final AsicEGenerator asicEGenerator;

    public EbmsForsendelseBuilder() {
        sdpBuilder = new SDPBuilder();
        asicEGenerator = new AsicEGenerator();
    }

    public EbmsForsendelse buildEbmsForsendelse(Avsender avsender, Forsendelse forsendelse) {
        Mottaker mottaker = forsendelse.getDigitalPost().getMottaker();
        
        Organisasjonsnummer avsenderOrganisasjonsnummer = new Organisasjonsnummer(avsender.getOrganisasjonsnummer());
        Organisasjonsnummer mottakerOrganisasjonsnummer = new Organisasjonsnummer(mottaker.getOrganisasjonsnummerPostkasse());
        SDPDigitalPost sikkerDigitalPost = createSikkerDigitalPost(avsender, forsendelse);
        InputStream dokumentpakke = asicEGenerator.createStream(avsender, forsendelse);

        return EbmsForsendelse.create(avsenderOrganisasjonsnummer, mottakerOrganisasjonsnummer, sikkerDigitalPost, dokumentpakke).build();
    }

    private SDPDigitalPost createSikkerDigitalPost(Avsender avsender, Forsendelse forsendelse) {
        return sdpBuilder.buildDigitalPost(avsender, forsendelse);
    }
}
