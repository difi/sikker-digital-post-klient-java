package no.difi.sdp.client2.internal;

import no.difi.sdp.client2.ExceptionMapper;
import no.difi.sdp.client2.domain.exceptions.NoekkelException;
import no.difi.sdp.client2.domain.exceptions.UgyldigTidsstempelException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.ws.soap.security.wss4j2.Wss4jSecurityValidationException;

public class UserFriendlyWsSecurityExceptionMapperTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void resolves_invalid_time_stamp_exception(){
        //Fordi denne er ganske vanskelig å tvinge fram i klienten så legger jeg ved en en kort beskrivelse her på hvordan den reproduseres:
        //Still klokken på maskinen til å være 1 minutt tidligere enn faktisk tid og kjør.

        UserFriendlyWsSecurityExceptionMapper exceptionMapper = new UserFriendlyWsSecurityExceptionMapper();
        Exception exception = new Wss4jSecurityValidationException("Invalid timestamp: The message timestamp is out of range");

        thrown.expect(UgyldigTidsstempelException.class);
        thrown.expectMessage("Ugyldig timestamp i sendt melding.");

        exceptionMapper.resolveException(null,null, exception);
    }

    @Test
    public void resolves_no_trusted_certs_found_exception(){
        UserFriendlyWsSecurityExceptionMapper exceptionMapper = new UserFriendlyWsSecurityExceptionMapper();
        Exception exception = new Wss4jSecurityValidationException("Error during certificate path validation: No trusted certs found");

        thrown.expect(NoekkelException.class);
        thrown.expectMessage("Klarte ikke å verifisere signatur på responsen.");

        exceptionMapper.resolveException(null,null, exception);
    }
}
