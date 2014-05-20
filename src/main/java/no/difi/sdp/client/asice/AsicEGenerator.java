package no.difi.sdp.client.asice;

import no.difi.sdp.client.domain.Forsendelse;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class AsicEGenerator {

    public InputStream createStream(Forsendelse forsendelse) {
        return new ByteArrayInputStream("todo".getBytes());
    }

}
