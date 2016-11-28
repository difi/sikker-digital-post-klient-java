package no.difi.sdp.client2.internal;

import no.difi.sdp.client2.domain.Noekkelpar;
import no.difi.sdp.client2.domain.exceptions.NoekkelException;
import no.difi.sdp.client2.domain.exceptions.UgyldigTidsstempelException;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.EndpointExceptionResolver;
import org.springframework.ws.soap.security.wss4j2.Wss4jSecurityValidationException;

public class UserFriendlyWsSecurityExceptionMapper implements EndpointExceptionResolver {
    @Override
    public boolean resolveException(MessageContext messageContext, Object endpoint, Exception ex) {
        if (ex instanceof Wss4jSecurityValidationException) {
            Wss4jSecurityValidationException securityValidationException = (Wss4jSecurityValidationException) ex;

            if (securityValidationException.getMessage().contains("Invalid timestamp: The message timestamp is out of range")) {
                throw new UgyldigTidsstempelException("Ugyldig timestamp i sendt melding. Dette kan skyldes at maskinen som klienten kjører på ikke har stilt klokken korrekt. Juster klokken etter verdensklokken og prøv igjen.", ex);
            }

            if (securityValidationException.getMessage().contains("No trusted certs found")) {
                throw new NoekkelException("Klarte ikke å verifisere signatur på responsen. Dette kan skyldes at det tiltrodde rotsertifikatet for Meldingsformidler mangler i keystore eller trust store. " +
                        "Bruk overload for " + Noekkelpar.class.getSimpleName() + " som bruker klientens innebygde sertifikater for trust store eller legg inn sertifikater. ");
            }
        }

        return false;
    }
}
