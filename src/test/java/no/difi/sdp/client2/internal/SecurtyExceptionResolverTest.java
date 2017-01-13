package no.difi.sdp.client2.internal;

import no.difi.sdp.client2.domain.exceptions.NoekkelException;
import no.difi.sdp.client2.domain.exceptions.UgyldigTidsstempelException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.ws.soap.security.wss4j2.Wss4jSecurityValidationException;

public class SecurtyExceptionResolverTest {

    private static SecurityExceptionResolver securityExceptionResolver = new SecurityExceptionResolver();

    public static class ResolveExceptionMethod {

        @Rule
        public ExpectedException thrown = ExpectedException.none();

        @Test
        public void resolves_resolves_possibly_incorrect_local_time_exception() {
            //Fordi denne er ganske vanskelig å tvinge fram i klienten så legger jeg ved en en kort beskrivelse her på hvordan den reproduseres i klienten:
            //Still klokken på maskinen til å være 1 minutt tidligere enn faktisk tid og kjør en smoketest.
            Exception invalidTimestampException = new Wss4jSecurityValidationException("Invalid timestamp: The message timestamp is out of range");

            thrown.expect(UgyldigTidsstempelException.class);
            securityExceptionResolver.resolveException(null, null, invalidTimestampException);
        }

        @Test
        public void resolves_no_trusted_certs_found() {
            Exception invalidTimestampException = new Wss4jSecurityValidationException("Error during certificate path validation: No trusted certs found");

            thrown.expect(NoekkelException.class);
            securityExceptionResolver.resolveException(null, null, invalidTimestampException);
        }
    }

}