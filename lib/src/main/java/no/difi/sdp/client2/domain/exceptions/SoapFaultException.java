package no.difi.sdp.client2.domain.exceptions;

import no.digipost.api.exceptions.MessageSenderSoapFaultException;

import static no.difi.sdp.client2.domain.exceptions.SendException.AntattSkyldig.fraSoapFaultCode;

public class SoapFaultException extends SendException {
    public SoapFaultException(MessageSenderSoapFaultException e) {
        super(e.getSoapFault().getFaultStringOrReason(), fraSoapFaultCode(e.getSoapFault().getFaultCode()), e);
    }
}
