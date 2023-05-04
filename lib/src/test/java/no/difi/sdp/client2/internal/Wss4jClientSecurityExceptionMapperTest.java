/*
 * Copyright (C) Posten Norge AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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