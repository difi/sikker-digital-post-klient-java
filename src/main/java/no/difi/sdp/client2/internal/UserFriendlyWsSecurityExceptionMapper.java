package no.difi.sdp.client2.internal;

import no.difi.sdp.client2.domain.Noekkelpar;
import no.difi.sdp.client2.domain.exceptions.NoekkelException;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.EndpointExceptionResolver;
import org.springframework.ws.soap.security.wss4j2.Wss4jSecurityValidationException;

public class UserFriendlyWsSecurityExceptionMapper implements EndpointExceptionResolver {
    @Override
    public boolean resolveException(MessageContext messageContext, Object endpoint, Exception ex) {
        if (ex instanceof Wss4jSecurityValidationException) {
            Wss4jSecurityValidationException securityValidationException = (Wss4jSecurityValidationException) ex;

            if(securityValidationException.getMessage().contains("Invalid timestamp")){
                throw new Wss4jSecurityValidationException("Ugyldig timestamp i sendt melding. Dette kan skyldes at klokken som klienten kjører på ikke er korrekt. Juster klokken etter verdensklokken og prøv igjen.", ex);
            }

            if(securityValidationException.getMessage().contains("No trusted certs found")){
                throw new NoekkelException("Klarte ikke å verifisere signatur på responsen. Dette kan skyldes at det tiltrodde rotsertifikatet mangler i keystore/trust store for Meldingsformidler. " +
                        "Bruk overload for" + Noekkelpar.class.getSimpleName() + " som bruker klientens innebygde sertifikater for trust store eller legg inn sertifikater funnet her. ");
            }


        }else if(ex instanceof org.apache.wss4j.common.ext.WSSecurityException){

        }

        return false;
    }
}
