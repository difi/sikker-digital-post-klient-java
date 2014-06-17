package no.difi.sdp.client;

import no.difi.sdp.client.domain.exceptions.TransportException;

public class TransportIOException extends TransportException {

    public TransportIOException(Exception e) {
        super(e.getMessage(), AntattSkyldig.UKJENT, e);
    }

}
