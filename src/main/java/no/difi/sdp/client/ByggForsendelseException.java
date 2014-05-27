package no.difi.sdp.client;

public class ByggForsendelseException extends RuntimeException {
    public ByggForsendelseException(String message, Exception e) {
        super (message, e);
    }
}
