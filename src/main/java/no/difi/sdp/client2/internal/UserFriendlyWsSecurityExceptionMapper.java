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
