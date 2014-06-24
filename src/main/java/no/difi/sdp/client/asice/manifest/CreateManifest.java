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
package no.difi.sdp.client.asice.manifest;

import no.difi.begrep.sdp.schema_v10.SDPManifest;
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

    public Manifest createManifest(Forsendelse forsendelse) {
        SDPManifest sdpManifest = sdpBuilder.createManifest(forsendelse);

        ByteArrayOutputStream manifestStream = new ByteArrayOutputStream();
        marshaller.marshal(sdpManifest, new StreamResult(manifestStream));
        return new Manifest(manifestStream.toByteArray());
    }

}
