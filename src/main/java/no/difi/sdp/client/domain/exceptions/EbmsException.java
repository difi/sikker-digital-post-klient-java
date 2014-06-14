package no.difi.sdp.client.domain.exceptions;

import no.digipost.api.EbmsClientException;

public class EbmsException extends TransportException {

    private final String errorCode;
    private final String errorDescription;

    public EbmsException(EbmsClientException e) {
        super(e.getError().getDescription().getValue(), AntattSkyldig.fraSoapFault(e.getSoapError()), e);

        errorCode = e.getError().getErrorCode();
        errorDescription = e.getError().getDescription().getValue();
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorDescription() {
        return errorDescription;
    }
}
