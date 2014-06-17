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
