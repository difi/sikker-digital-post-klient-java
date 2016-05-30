package no.difi.sdp.client2.domain.exceptions;

import no.digipost.api.exceptions.MessageSenderEbmsErrorException;

public class EbmsException extends SendException {

    private final String errorCode;
    private final String errorDescription;

    public EbmsException(MessageSenderEbmsErrorException e) {
        super(createMessage(e), AntattSkyldig.fraSoapFaultCode(e.getSoapFault().getFaultCode()), e);

        errorCode = e.getError().getErrorCode();
        errorDescription = e.getError().getDescription().getValue();
    }

    private static String createMessage(MessageSenderEbmsErrorException e) {
        String message = "";
        if (e.getError() != null) {
            message += e.getError().getErrorCode();

            if (e.getError().getDescription() != null) {
                message += " - " + e.getMessage();
            }

            return message;
        }
        return "An unknown ebMS error has occured.";
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorDescription() {
        return errorDescription;
    }
}
