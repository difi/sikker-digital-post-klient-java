package no.difi.sdp.client.domain.exceptions;

public class KonfigurasjonException extends RuntimeException {
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
