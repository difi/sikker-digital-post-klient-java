package no.difi.sdp.client2;

import no.difi.sdp.client2.domain.Noekkelpar;
import no.difi.sdp.client2.domain.exceptions.EbmsException;
import no.difi.sdp.client2.domain.exceptions.KonfigurasjonException;
import no.difi.sdp.client2.domain.exceptions.NoekkelException;
import no.difi.sdp.client2.domain.exceptions.SendException;
import no.difi.sdp.client2.domain.exceptions.SendIOException;
import no.difi.sdp.client2.domain.exceptions.SikkerDigitalPostException;
import no.difi.sdp.client2.domain.exceptions.SoapFaultException;
import no.difi.sdp.client2.domain.exceptions.UgyldigTidsstempelException;
import no.difi.sdp.client2.domain.exceptions.ValideringException;
import no.digipost.api.exceptions.MessageSenderEbmsErrorException;
import no.digipost.api.exceptions.MessageSenderIOException;
import no.digipost.api.exceptions.MessageSenderSoapFaultException;
import no.digipost.api.exceptions.MessageSenderValidationException;
import org.springframework.ws.soap.security.wss4j2.Wss4jSecurityValidationException;

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
        KonfigurasjonException userFriendlyException = resolveToUserFriendlyExceptionIfPossible(e);
        if(userFriendlyException != null){
            return userFriendlyException;
        }

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
        return null;
    }

    private KonfigurasjonException resolveToUserFriendlyExceptionIfPossible(Exception e) {
        boolean isPossiblyIncorrectLocalTimeException = (e instanceof Wss4jSecurityValidationException || e instanceof MessageSenderEbmsErrorException) && e.getMessage().contains("Invalid timestamp: The message timestamp is out of range");
        if(isPossiblyIncorrectLocalTimeException) {
            return new UgyldigTidsstempelException("Ugyldig timestamp i sendt melding. Dette kan skyldes at maskinen som klienten kjører på ikke har stilt klokken korrekt.", e);
        }

        boolean isCouldNotFindTrustedCertificatesException = (e instanceof Wss4jSecurityValidationException && e.getMessage().contains("No trusted certs found"));
        if(isCouldNotFindTrustedCertificatesException) {
            return new NoekkelException("Klarte ikke å verifisere signatur på responsen. Dette kan skyldes at det tiltrodde rotsertifikatet for Meldingsformidler mangler i keystore eller trust store. " +
                    "Bruk overload for " + Noekkelpar.class.getSimpleName() + " som bruker klientens innebygde sertifikater for trust store eller legg inn sertifikater. ");
        }

        return null;
    }
}
