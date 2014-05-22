package no.difi.sdp.client.internal;

import no.difi.sdp.client.asice.CreateAsicE;
import no.difi.sdp.client.domain.Avsender;
import no.difi.sdp.client.domain.Forsendelse;

import java.io.InputStream;

public class CreateDokumentpakke {

    private final CreateAsicE createAsicE;

    public CreateDokumentpakke() {
        createAsicE = new CreateAsicE();
    }

    public InputStream createDokumentpakke(Avsender avsender, Forsendelse forsendelse) {
        return createAsicE.createStream(avsender, forsendelse);
    }

}
