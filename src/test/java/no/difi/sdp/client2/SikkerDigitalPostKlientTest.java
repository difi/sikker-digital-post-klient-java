/**
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
package no.difi.sdp.client2;

import no.difi.sdp.client2.domain.exceptions.SendIOException;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static no.difi.sdp.client2.ObjectMother.forsendelse;
import static no.difi.sdp.client2.ObjectMother.tekniskAvsender;
import static no.difi.sdp.client2.domain.exceptions.SendException.AntattSkyldig.UKJENT;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Fail.fail;

public class SikkerDigitalPostKlientTest {

    @Test
    public void haandter_connection_timeouts() {
        String lokalTimeoutUrl = "http://10.255.255.1/";
        KlientKonfigurasjon klientKonfigurasjon = KlientKonfigurasjon.builder()
                .meldingsformidlerRoot(lokalTimeoutUrl)
                .connectionTimeout(1, TimeUnit.MILLISECONDS)
                .build();

        SikkerDigitalPostKlient postklient = new SikkerDigitalPostKlient(tekniskAvsender(), klientKonfigurasjon);

        try {
            postklient.send(forsendelse());
            fail("Should fail");
        }
        catch (SendIOException e) {
            assertThat(e.getAntattSkyldig()).isEqualTo(UKJENT);
        }
    }

    @Test
    public void kall_http_interceptors() {
        final StringBuffer interceptorString = new StringBuffer();

        String lokalTimeoutUrl = "http://10.255.255.1/";
        KlientKonfigurasjon klientKonfigurasjon = KlientKonfigurasjon.builder()
                .meldingsformidlerRoot(lokalTimeoutUrl)
                .connectionTimeout(1, TimeUnit.MILLISECONDS)
                .httpRequestInterceptors(new HttpRequestInterceptor() {
                    @Override
                    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
                        interceptorString.append("First interceptor called");
                    }
                }, new HttpRequestInterceptor() {
                    @Override
                    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
                        interceptorString.append(", and second too!");
                    }
                })
                .build();

        SikkerDigitalPostKlient postklient = new SikkerDigitalPostKlient(tekniskAvsender(), klientKonfigurasjon);

        try {
            postklient.send(forsendelse());
            fail("Fails");
        }
        catch (SendIOException e) {
            assertThat(interceptorString.toString()).isEqualTo("First interceptor called, and second too!");
        }
    }

}
