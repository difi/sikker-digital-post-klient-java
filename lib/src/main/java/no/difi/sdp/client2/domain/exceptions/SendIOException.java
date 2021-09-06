package no.difi.sdp.client2.domain.exceptions;

public class SendIOException extends SendException {

    public SendIOException(Exception e) {
        super(e.getMessage(), AntattSkyldig.UKJENT, e);
    }

}
