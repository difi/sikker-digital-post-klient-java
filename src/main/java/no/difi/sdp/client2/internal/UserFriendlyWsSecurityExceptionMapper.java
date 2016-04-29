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
package no.difi.sdp.client2.internal;

import no.difi.sdp.client2.domain.exceptions.NoekkelException;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.EndpointExceptionResolver;
import org.springframework.ws.soap.security.wss4j.Wss4jSecurityValidationException;

public class UserFriendlyWsSecurityExceptionMapper implements EndpointExceptionResolver {
    @Override
    public boolean resolveException(MessageContext messageContext, Object endpoint, Exception ex) {
        if (ex instanceof Wss4jSecurityValidationException) {
            throw new NoekkelException("Klarte ikke å verifisere signatur på responsen. " +
                    "Dette kan skyldes at det tiltrodde rotsertifikatet mangler i keystore/trust store. " +
                    "Hvis dette plutselig begynner å skje kan det skyldes at den andre parten har skiftet sertifikat", ex);
        }
        return false;
    }
}
