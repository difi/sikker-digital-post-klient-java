package no.difi.sdp.client2.domain.exceptions;

import no.digipost.api.exceptions.MessageSenderValidationException;

import static no.difi.sdp.client2.domain.exceptions.SendException.AntattSkyldig.SERVER;


/**
 * Indikerer at server har returnert data som ikke validerer i henhold til forventning
 * Eksempler: feil org nummer, manglende ebMS Messaging header
 */
public class ValideringException extends SendException {

    public ValideringException(MessageSenderValidationException e) {
        super(e.getMessage(), SERVER, e);
    }

}
