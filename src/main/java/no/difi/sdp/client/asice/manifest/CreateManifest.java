package no.difi.sdp.client.asice.manifest;


import no.difi.begrep.sdp.schema_v10.SDPManifest;
import no.difi.sdp.client.domain.Avsender;
import no.difi.sdp.client.domain.Forsendelse;
import no.difi.sdp.client.internal.SDPBuilder;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;

public class CreateManifest {

    private static final Jaxb2Marshaller marshaller;

    static {
        marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(SDPManifest.class);
    }

    private final SDPBuilder sdpBuilder;

    public CreateManifest() {
        sdpBuilder = new SDPBuilder();
    }

    public Manifest createManifest(Avsender avsender, Forsendelse forsendelse) {
        SDPManifest sdpManifest = sdpBuilder.createManifest(avsender, forsendelse);

        ByteArrayOutputStream manifestStream = new ByteArrayOutputStream();
        marshaller.marshal(sdpManifest, new StreamResult(manifestStream));
        return new Manifest(manifestStream.toByteArray());
    }

}
