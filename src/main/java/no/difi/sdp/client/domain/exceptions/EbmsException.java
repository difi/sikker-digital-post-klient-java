package no.difi.sdp.client.domain.exceptions;

import no.digipost.api.EbmsClientException;

public class EbmsException extends RuntimeException {
    public EbmsException(EbmsClientException e) {
        // TODO: Parse exception to gain as much meaningful info as possible
    }
}
