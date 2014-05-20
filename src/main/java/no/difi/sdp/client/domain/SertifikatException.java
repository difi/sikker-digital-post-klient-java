package no.difi.sdp.client.domain;

public class SertifikatException extends RuntimeException {
    public SertifikatException(String message, Exception e) {
        super(message, e);
    }

    public SertifikatException(String message) {
        super(message);
    }
}
