package no.difi.sdp.client.asice;

import no.difi.sdp.client.domain.Avsender;
import no.difi.sdp.client.domain.Forsendelse;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class AsicEGenerator {

    private final CreateManifest createManifest;
    private final CreateSignature createSignature;

    public AsicEGenerator() {
        createManifest = new CreateManifest();
        createSignature = new CreateSignature();
    }

    public InputStream createStream(Avsender avsender, Forsendelse forsendelse) {
        // Generate manifest
        Manifest manifest = createManifest.createManifest(avsender, forsendelse);

        Signature signature = createSignature.createSignature(manifest, avsender, forsendelse);

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
