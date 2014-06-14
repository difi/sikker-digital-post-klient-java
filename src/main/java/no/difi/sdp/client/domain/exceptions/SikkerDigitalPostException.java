package no.difi.sdp.client.domain.exceptions;

public class SikkerDigitalPostException extends RuntimeException {

    public SikkerDigitalPostException() { }

    public SikkerDigitalPostException(String message, Exception e) {
        super(message, e);
    }

    public SikkerDigitalPostException(Exception e) {
        super(e);
    }
}
