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
package no.difi.sdp.client2.asice.signature;

import no.digipost.api.xml.JaxbMarshaller;
import org.etsi.uri._01903.v1_3.QualifyingProperties;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static java.util.Collections.singleton;
import static java.util.stream.IntStream.range;

final class XAdESArtifacts {

    private static JaxbMarshaller marshaller = JaxbMarshaller.marshallerForClasses(singleton(QualifyingProperties.class));


    public static XAdESArtifacts from(QualifyingProperties qualifyingProperties) {
        Document qualifyingPropertiesAsDomDocument = marshaller.marshalToDomDocument(qualifyingProperties);
        return from(qualifyingPropertiesAsDomDocument);
    }

    private static XAdESArtifacts from(Document qualifyingPropertiesDocument) {
        Element qualifyingProperties = qualifyingPropertiesDocument.getDocumentElement();
        NodeList qualifyingPropertiesContents = qualifyingProperties.getChildNodes();
        Element signedProperties = range(0, qualifyingPropertiesContents.getLength()).mapToObj(qualifyingPropertiesContents::item)
            .filter(node -> node.getNodeType() == Node.ELEMENT_NODE)
            .map(Element.class::cast)
            .filter(element -> "SignedProperties".equals(element.getLocalName()))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Didn't find SignedProperties in document."));
        String signerPropertiesReferenceUri = signedProperties.getAttribute("Id");
        return new XAdESArtifacts(qualifyingPropertiesDocument, signedProperties, "#" + signerPropertiesReferenceUri);
    }


    public final Document document;
    public final Element signableProperties;
    public final String signablePropertiesReferenceUri;

    private XAdESArtifacts(Document document, Element signableProperties, String signerPropertiesReferenceUri) {
        this.document = document;
        this.signableProperties = signableProperties;
        this.signablePropertiesReferenceUri = signerPropertiesReferenceUri;
    }

}