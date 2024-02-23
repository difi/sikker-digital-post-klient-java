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
import no.difi.sdp.client2.domain.exceptions.KonfigurasjonException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static javax.xml.xpath.XPathConstants.NODESET;

class DomUtils {

    private final DocumentBuilderFactory documentBuilderFactory;
    private final TransformerFactory transformerFactory;

    DomUtils() {
        transformerFactory = TransformerFactory.newInstance();
        documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
    }


    public Document newEmptyXmlDocument() {
        try {
            return documentBuilderFactory.newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            throw new KonfigurasjonException("Unable to create new Document. " + e.getClass().getSimpleName() + ": '" + e.getMessage() + "'", e);
        }
    }

    public Stream<Node> allNodesBelow(Node node) {
        try {
            NodeList nodeList = (NodeList) XPathFactory.newInstance().newXPath().evaluate(". | .//node() | .//@*", node, NODESET);
            return IntStream.range(0, nodeList.getLength()).mapToObj(i -> nodeList.item(i));
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public byte[] serializeToXml(Node root) {
        byte[] serializedXml;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Transformer transformer = transformerFactory.newTransformer();
            transformer.transform(new DOMSource(root), new StreamResult(outputStream));
            serializedXml = outputStream.toByteArray();
        } catch (TransformerException | IOException e) {
            throw new KonfigurasjonException("Unable to serialize XML because " + e.getClass().getSimpleName() + ": '" + e.getMessage(), e);
        }
        return serializedXml;
    }


}