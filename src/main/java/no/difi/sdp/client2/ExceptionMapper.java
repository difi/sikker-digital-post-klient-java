package no.difi.sdp.client2;

import no.difi.sdp.client2.domain.exceptions.EbmsException;
import no.difi.sdp.client2.domain.exceptions.SendException;
import no.difi.sdp.client2.domain.exceptions.SendIOException;
import no.difi.sdp.client2.domain.exceptions.SoapFaultException;
import no.difi.sdp.client2.domain.exceptions.ValideringException;
import no.digipost.api.exceptions.MessageSenderEbmsErrorException;
import no.digipost.api.exceptions.MessageSenderIOException;
import no.digipost.api.exceptions.MessageSenderSoapFaultException;
import no.digipost.api.exceptions.MessageSenderValidationException;

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
    public SendException mapException(Exception e) {
        // Don't attempt to map SendExceptions, they are already correct type
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
}
