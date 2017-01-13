package no.difi.sdp.client2.internal;

import no.difi.sdp.client2.domain.Noekkelpar;
import no.difi.sdp.client2.domain.exceptions.NoekkelException;
import no.difi.sdp.client2.domain.exceptions.UgyldigTidsstempelException;
import no.digipost.api.exceptions.MessageSenderEbmsErrorException;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.EndpointExceptionResolver;
import org.springframework.ws.soap.security.wss4j2.Wss4jSecurityValidationException;

/**
 * Used to resolve security related exceptions.
 */
class SecurityExceptionResolver implements EndpointExceptionResolver {

    @Override
    public boolean resolveException(MessageContext messageContext, Object o, Exception e) {
        boolean resolved = false;

        boolean isPossiblyIncorrectLocalTimeException = (e instanceof Wss4jSecurityValidationException || e instanceof MessageSenderEbmsErrorException) && e.getMessage().contains("Invalid timestamp: The message timestamp is out of range");
        if(isPossiblyIncorrectLocalTimeException) {
            resolved = true;
            throw new UgyldigTidsstempelException("Ugyldig timestamp i sendt melding. Dette kan skyldes at maskinen som klienten kjører på ikke har stilt klokken korrekt.", e);
        }

        boolean isCouldNotFindTrustedCertificatesException = (e instanceof Wss4jSecurityValidationException && e.getMessage().contains("No trusted certs found"));
        if(isCouldNotFindTrustedCertificatesException) {
            resolved = true;
            throw new NoekkelException("Klarte ikke å verifisere signatur på responsen. Dette kan skyldes at det tiltrodde rotsertifikatet for Meldingsformidler mangler i keystore eller trust store. " +
                    "Bruk overload for " + Noekkelpar.class.getSimpleName() + " som bruker klientens innebygde sertifikater for trust store eller legg inn mellomliggende sertifikat og rotsertifikat " +
                    "til Buypass og Commfides i trust store.");
        }

        return resolved;
    }
}
