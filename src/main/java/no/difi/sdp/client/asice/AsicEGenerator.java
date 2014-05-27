package no.difi.sdp.client.asice;

import no.difi.begrep.sdp.schema_v10.SDPManifest;
import no.difi.sdp.client.domain.Avsender;
import no.difi.sdp.client.domain.Forsendelse;
import no.difi.sdp.client.internal.SDPBuilder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class AsicEGenerator {

    private final SDPBuilder sdpBuilder;

    public AsicEGenerator() {
        sdpBuilder = new SDPBuilder();
    }

    public InputStream createStream(Avsender avsender, Forsendelse forsendelse) {
        // Generate manifest
        SDPManifest sdpManifest = sdpBuilder.createManifest(avsender, forsendelse);

        /**
         * 1. Generate Manifest
         * 2. Generate Signatures.xml
         *  * Hashing
         *  * Signatures
         * 3. Zip
         */
        return new ByteArrayInputStream("todo".getBytes());
    }

}
