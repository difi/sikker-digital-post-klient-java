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
package no.difi.sdp.client2.internal;

import no.difi.sdp.client2.domain.Noekkelpar;
import no.difi.sdp.client2.domain.exceptions.NoekkelException;
import no.difi.sdp.client2.domain.exceptions.SendException;
import no.difi.sdp.client2.domain.exceptions.UgyldigTidsstempelException;
import no.digipost.api.exceptions.MessageSenderEbmsErrorException;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.EndpointExceptionResolver;
import org.springframework.ws.soap.security.wss4j2.Wss4jSecurityValidationException;

import static no.difi.sdp.client2.domain.exceptions.SendException.AntattSkyldig.UKJENT;

/**
 * Even if this class implements the {@see org.springframework.ws.server.EndpointExceptionResolver}, this implementation is injected into our {@see no.digipost.api.interceptors.WsSecurityInterceptor},
 * which is a client interceptor in the context of this library. An {@see org.springframework.ws.server.EndpointExceptionResolver} is supposed to be used by the message dispatcher on the server side.
 * This is why this resolver is used like an exception mapper instead of an exception resolver, and is just a way of dealing with a broken framework. When looking at how exceptions are handled by other
 * client interceptors, for instance {@see org.springframework.ws.client.support.interceptor.PayloadValidatingInterceptor} it is clear that the exceptions are supposed to be thrown in client interceptors
 * in order to handle control flow.
 */
class Wss4jClientSecurityExceptionMapper implements EndpointExceptionResolver {

    /**
     * Maps undlerlying security exceptions into proper, human readable exceptions.
     *
     * @param messageContext
     * @param o
     * @param e
     * @return Will always throw exceptions, so there will never be a return value of true/false. This is contradictory to the interface documentation of {@see EndpointExceptionResolver}
     */
    @Override
    public boolean resolveException(MessageContext messageContext, Object o, Exception e) {
        if (e.getMessage() != null) {
            boolean isPossiblyIncorrectLocalTimeException =
                    (e instanceof Wss4jSecurityValidationException || e instanceof MessageSenderEbmsErrorException)
                            && e.getMessage().contains("Invalid timestamp: The message timestamp is out of range");
            if (isPossiblyIncorrectLocalTimeException) {
                throw new UgyldigTidsstempelException("Ugyldig timestamp i sendt melding. Dette kan skyldes at maskinen som klienten kjører på ikke har stilt klokken korrekt.", e);
            }

            boolean isCouldNotFindTrustedCertificatesException = (e instanceof Wss4jSecurityValidationException && e.getMessage().contains("No trusted certs found"));
            if (isCouldNotFindTrustedCertificatesException) {
                throw new NoekkelException("Klarte ikke å verifisere signatur på responsen. Dette kan skyldes at det tiltrodde rotsertifikatet for Meldingsformidler mangler i keystore eller trust store. " +
                        "Bruk overload for " + Noekkelpar.class.getSimpleName() + " som bruker klientens innebygde sertifikater for trust store eller legg inn mellomliggende sertifikat og rotsertifikat " +
                        "til Buypass og Commfides i trust store.");
            }
        }

        throw new SendException("Under mapping av exception skjedde en feil som ikke kunne håndteres.", UKJENT, e);
    }
}
