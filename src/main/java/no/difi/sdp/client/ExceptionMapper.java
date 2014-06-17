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
package no.difi.sdp.client;

import no.difi.sdp.client.domain.exceptions.EbmsException;
import no.digipost.api.EbmsClientException;
import org.springframework.ws.client.WebServiceIOException;

/**
 * Exception mapper for sending av sikker digital post. Gjør subclassing av denne for implementere egen/tilpasset feilhåndtering.
 * Bruk kall til super for å ta med innebygd feilhåndtering.
 */
public class ExceptionMapper {

    /**
     * Oversetter Exceptions kastet fra de underliggende lagene.
     *
     * @param e original exception
     * @return Mappet exception som skal kastes. null dersom ingen mapping er gjort og opprinnelig exception skal brukes.
     */
    public RuntimeException mapException(Exception e) {
        if (e instanceof EbmsClientException) {
            return new EbmsException((EbmsClientException) e);
        }
        else if(e instanceof WebServiceIOException) {
            return new TransportIOException(e);
        }
        return null;
    }
}
