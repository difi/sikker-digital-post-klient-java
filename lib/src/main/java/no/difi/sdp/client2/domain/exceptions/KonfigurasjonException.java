package no.difi.sdp.client2.domain.exceptions;

public class KonfigurasjonException extends SikkerDigitalPostException {

    public KonfigurasjonException(String message, Exception e) {
        super(message, e);
    }

    public KonfigurasjonException(String message) {
        this(message, null);
    }

    public KonfigurasjonException(Exception e) {
        this(null, e);
    }

}
