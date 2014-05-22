package no.difi.sdp.client.asice.manifest;


import no.difi.begrep.sdp.schema_v10.SDPManifest;
import no.difi.sdp.client.ByggForsendelseException;
import no.difi.sdp.client.util.Jaxb;
import no.difi.sdp.client.domain.Avsender;
import no.difi.sdp.client.domain.Forsendelse;
import no.difi.sdp.client.internal.SDPBuilder;

import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CreateManifest {

    private final SDPBuilder sdpBuilder;

    public CreateManifest() {
        sdpBuilder = new SDPBuilder();
    }

    public Manifest createManifest(Avsender avsender, Forsendelse forsendelse) {
        SDPManifest sdpManifest = sdpBuilder.createManifest(avsender, forsendelse);

        try {
            ByteArrayOutputStream manifestStream = new ByteArrayOutputStream();
            Jaxb.marshaller().marshal(sdpManifest, new StreamResult(manifestStream));
            return new Manifest(manifestStream.toByteArray());
        } catch (IOException e) {
            throw new ByggForsendelseException("Kunne ikke lage Asic-E manifest", e);
        }
    }

}
