package no.difi.sdp.client2.domain.exceptions;

public class SertifikatException extends KonfigurasjonException {

    public SertifikatException(String message, Exception e) {
        super(message, e);
    }

    public SertifikatException(String message) {
        super(message);
    }

}
