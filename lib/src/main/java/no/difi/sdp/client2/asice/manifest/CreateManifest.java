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
import no.difi.sdp.client2.domain.exceptions.SendException;
import no.difi.sdp.client2.domain.exceptions.XmlValideringException;
import no.difi.sdp.client2.internal.SDPBuilder;
import no.digipost.api.xml.JaxbMarshaller;
import no.digipost.api.xml.MarshallingException;
import no.digipost.api.xml.SchemaResources;
import org.xml.sax.SAXParseException;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;

public class CreateManifest {

    private static final JaxbMarshaller marshaller = JaxbMarshaller.validatingMarshallerForClasses(asList(SDPManifest.class), singleton(SchemaResources.SDP_MANIFEST_SCHEMA));

    private final SDPBuilder sdpBuilder;

    public CreateManifest() {
        sdpBuilder = new SDPBuilder();
    }

    public Manifest createManifest(Forsendelse forsendelse) {
        SDPManifest sdpManifest = sdpBuilder.createManifest(forsendelse);
        try {
            return new Manifest(marshaller.marshalToBytes(sdpManifest));
        } catch (MarshallingException e) {
            for (Throwable cause = e.getCause(); cause != null; cause = cause.getCause()) {
                if (cause instanceof SAXParseException) {
                    throw new XmlValideringException("Kunne ikke validere generert Manifest XML. Sjekk at alle påkrevde input er satt og ikke er null",
                            SendException.AntattSkyldig.KLIENT, e);
                }
            }
            throw e;
        }

    }

}
