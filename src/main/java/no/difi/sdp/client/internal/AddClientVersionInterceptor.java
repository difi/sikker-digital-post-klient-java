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
package no.difi.sdp.client.internal;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

public class AddClientVersionInterceptor implements HttpRequestInterceptor {

    private final String clientVersion;
    private final String javaVersion;

    public AddClientVersionInterceptor() {
        String implementationVersion = getClass().getPackage().getImplementationVersion();
        String javaVersion = System.getProperty("java.version");
        this.clientVersion = implementationVersion != null ? implementationVersion : "UNKNOWN";
        this.javaVersion = javaVersion != null ? javaVersion : "UNKNOWN";
    }

    @Override
    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
        Header[] headers = request.getHeaders("User-Agent");
        String clientUserAgent = "Java/" + javaVersion + " DifiSdp/" + clientVersion;
        if (headers.length == 0) {
            request.addHeader("User-Agent", clientUserAgent);
        }
        else {
            request.addHeader("User-Agent", headers[0].getValue() + " " + clientUserAgent);
        }
    }

}
