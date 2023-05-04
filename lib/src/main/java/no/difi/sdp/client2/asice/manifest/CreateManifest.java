/*
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
package no.difi.sdp.client2.asice.manifest;

import no.difi.begrep.sdp.schema_v10.SDPManifest;
import no.difi.sdp.client2.domain.Forsendelse;
import no.difi.sdp.client2.domain.exceptions.KonfigurasjonException;
import no.difi.sdp.client2.domain.exceptions.SendException;
import no.difi.sdp.client2.domain.exceptions.XmlValideringException;
import no.difi.sdp.client2.internal.SDPBuilder;
import no.digipost.api.xml.Schemas;
import org.springframework.oxm.MarshallingFailureException;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.xml.sax.SAXParseException;

import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;

public class CreateManifest {

    private static final Jaxb2Marshaller marshaller;

    static {
        marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(SDPManifest.class);
        marshaller.setSchema(Schemas.SDP_MANIFEST_SCHEMA);
        try {
            marshaller.afterPropertiesSet();
        } catch (Exception e) {
            throw new KonfigurasjonException("Kunne ikke sette opp Jaxb marshaller", e);
        }
    }

    private final SDPBuilder sdpBuilder;

    public CreateManifest() {
        sdpBuilder = new SDPBuilder();
    }

    public Manifest createManifest(Forsendelse forsendelse) {
        SDPManifest sdpManifest = sdpBuilder.createManifest(forsendelse);

        ByteArrayOutputStream manifestStream = new ByteArrayOutputStream();
        try {
            marshaller.marshal(sdpManifest, new StreamResult(manifestStream));
            return new Manifest(manifestStream.toByteArray());
        }
        catch(MarshallingFailureException e) {
            if (e.getMostSpecificCause() instanceof SAXParseException) {
                throw new XmlValideringException("Kunne ikke validere generert Manifest XML. Sjekk at alle påkrevde input er satt og ikke er null",
                        SendException.AntattSkyldig.KLIENT, (SAXParseException) e.getMostSpecificCause());
            }

            throw e;
        }

    }

}
