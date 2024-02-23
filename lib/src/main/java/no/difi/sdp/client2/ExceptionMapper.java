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
package no.difi.sdp.client2;

import no.difi.sdp.client2.domain.exceptions.EbmsException;
import no.difi.sdp.client2.domain.exceptions.SendException;
import no.difi.sdp.client2.domain.exceptions.SendIOException;
import no.difi.sdp.client2.domain.exceptions.SikkerDigitalPostException;
import no.difi.sdp.client2.domain.exceptions.SoapFaultException;
import no.difi.sdp.client2.domain.exceptions.ValideringException;
import no.digipost.api.exceptions.MessageSenderEbmsErrorException;
import no.digipost.api.exceptions.MessageSenderIOException;
import no.digipost.api.exceptions.MessageSenderSoapFaultException;
import no.digipost.api.exceptions.MessageSenderValidationException;

import static no.difi.sdp.client2.domain.exceptions.SendException.AntattSkyldig.UKJENT;

/**
 * Exception mapper for sending av sikker digital post. Gjør subclassing av denne for implementere egen/tilpasset feilhåndtering.
 * Bruk kall til super for å ta med innebygd feilhåndtering.
 */
public class ExceptionMapper {

    /**
     * Oversetter Exceptions kastet fra de underliggende lagene under sending av post.
     *
     * @param e original exception
     * @return Mappet exception som skal kastes. null dersom ingen mapping er gjort og opprinnelig exception skal brukes.
     */
    public SikkerDigitalPostException mapException(Exception e) {
        if (e instanceof SendException) {
            return (SendException) e;
        } else if (e instanceof MessageSenderEbmsErrorException) {
            return new EbmsException((MessageSenderEbmsErrorException) e);
        } else if (e instanceof MessageSenderIOException) {
            return new SendIOException(e);
        } else if (e instanceof MessageSenderValidationException) {
            return new ValideringException((MessageSenderValidationException) e);
        } else if (e instanceof MessageSenderSoapFaultException) {
            return new SoapFaultException((MessageSenderSoapFaultException) e);
        }

        return new SendException("En uhåndtert feil skjedde under sending", UKJENT, e);
    }
}
