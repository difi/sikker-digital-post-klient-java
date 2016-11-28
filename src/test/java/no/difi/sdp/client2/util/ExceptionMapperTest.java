package no.difi.sdp.client2.util;

import no.difi.sdp.client2.ExceptionMapper;
import no.difi.sdp.client2.domain.exceptions.EbmsException;
import no.difi.sdp.client2.domain.exceptions.NoekkelException;
import no.difi.sdp.client2.domain.exceptions.SendException;
import no.difi.sdp.client2.domain.exceptions.SendIOException;
import no.difi.sdp.client2.domain.exceptions.SoapFaultException;
import no.difi.sdp.client2.domain.exceptions.UgyldigTidsstempelException;
import no.difi.sdp.client2.domain.exceptions.ValideringException;
import no.digipost.api.exceptions.MessageSenderEbmsErrorException;
import no.digipost.api.exceptions.MessageSenderIOException;
import no.digipost.api.exceptions.MessageSenderSoapFaultException;
import no.digipost.api.exceptions.MessageSenderValidationException;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mockito;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Description;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.security.wss4j2.Wss4jSecurityValidationException;


import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

public class ExceptionMapperTest {

    @Test
    public void resolves_possibly_incorrect_local_time_exception(){
        //Fordi denne er ganske vanskelig å tvinge fram i klienten så legger jeg ved en en kort beskrivelse her på hvordan den reproduseres:
        //Still klokken på maskinen til å være 1 minutt tidligere enn faktisk tid og kjør en smoke test.

        ExceptionMapper exceptionMapper = new ExceptionMapper();
        Exception sourceException = new Wss4jSecurityValidationException("Invalid timestamp: The message timestamp is out of range");

        Exception mappedException = exceptionMapper.mapException(sourceException);

        assertThat(mappedException, instanceOf(UgyldigTidsstempelException.class));

    }

    @Test
    public void resolves_no_trusted_certs_found_exception(){
        ExceptionMapper exceptionMapper = new ExceptionMapper();
        Exception sourceException = new Wss4jSecurityValidationException("Error during certificate path validation: No trusted certs found");

        Exception mappedException = exceptionMapper.mapException(sourceException);

        assertThat(mappedException, instanceOf(NoekkelException.class));
    }

    @Test
    public void returns_send_exception_untouched(){
        ExceptionMapper exceptionMapper = new ExceptionMapper();
        Exception source = new SendException(null, null, null);

        Exception actual = exceptionMapper.mapException(source);

        assertThat(actual, sameInstance(source));
    }

    @Test
    public void to_ebms_exception(){
        ExceptionMapper exceptionMapper = new ExceptionMapper();

        Error error = new Error();
        error.setDescription(new Description("Dat Value", "nb-no"));

        SoapMessage soapMessage = Mockito.mock(SoapMessage.class, Mockito.withSettings().defaultAnswer(Answers.RETURNS_DEEP_STUBS));
        Exception source = new MessageSenderEbmsErrorException(soapMessage, error);

        Exception actual = exceptionMapper.mapException(source);

        assertThat(actual, instanceOf(EbmsException.class));
    }

    @Test
    public void to_send_io_exception(){
        ExceptionMapper exceptionMapper = new ExceptionMapper();
        Exception source = new MessageSenderIOException(null, null);

        Exception actual = exceptionMapper.mapException(source);

        assertThat(actual, instanceOf(SendIOException.class));
    }

    @Test
    public void to_validering_exception(){
        ExceptionMapper exceptionMapper = new ExceptionMapper();
        Exception source = new MessageSenderValidationException(null);

        Exception actual = exceptionMapper.mapException(source);

        assertThat(actual, instanceOf(ValideringException.class));
    }

    @Test
    public void to_soap_fault_exception(){
        ExceptionMapper exceptionMapper = new ExceptionMapper();

        SoapMessage soapMessage = Mockito.mock(SoapMessage.class, Mockito.withSettings().defaultAnswer(Answers.RETURNS_DEEP_STUBS));


        Exception source = new MessageSenderSoapFaultException(soapMessage);

        Exception actual = exceptionMapper.mapException(source);

        assertThat(actual, instanceOf(SoapFaultException.class));
    }
}
