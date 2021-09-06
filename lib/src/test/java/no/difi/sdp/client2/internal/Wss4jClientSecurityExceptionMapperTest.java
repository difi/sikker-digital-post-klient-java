package no.difi.sdp.client2.internal;

import no.difi.sdp.client2.domain.exceptions.UgyldigTidsstempelException;
import org.junit.jupiter.api.Test;
import org.springframework.ws.soap.security.wss4j2.Wss4jSecurityValidationException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class Wss4jClientSecurityExceptionMapperTest {

    private static Wss4jClientSecurityExceptionMapper wss4jClientSecurityExceptionMapper = new Wss4jClientSecurityExceptionMapper();

    public static class ResolveExceptionMethod {

        @Test
        public void resolves_resolves_possibly_incorrect_local_time_exception() {
            //Fordi denne er ganske vanskelig å tvinge fram i klienten så legger jeg ved en en kort beskrivelse her på hvordan den reproduseres i klienten:
            //Still klokken på maskinen til å være 1 minutt tidligere enn faktisk tid og kjør en smoketest.
            Exception invalidTimestampException = new Wss4jSecurityValidationException("Invalid timestamp: The message timestamp is out of range");

            assertThrows(UgyldigTidsstempelException.class, () -> wss4jClientSecurityExceptionMapper.resolveException(null, null, invalidTimestampException));
        }

        @Test
        public void resolves_no_trusted_certs_found() {
            Exception invalidTimestampException = new Wss4jSecurityValidationException("Error during certificate path validation: No trusted certs found");

            assertThrows(UgyldigTidsstempelException.class, () -> wss4jClientSecurityExceptionMapper.resolveException(null, null, invalidTimestampException));
        }

        @Test
        public void handles_null_exception_message() {
            Exception exceptionWithNullMessage = new Wss4jSecurityValidationException(null);

            assertThrows(UgyldigTidsstempelException.class, () -> wss4jClientSecurityExceptionMapper.resolveException(null, null, exceptionWithNullMessage));
        }
    }

}