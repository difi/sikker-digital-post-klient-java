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
package no.difi.sdp.client;

import no.digipost.api.representations.Organisasjonsnummer;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class KlientKonfigurasjon {

    private URI meldingsformidlerRoot = URI.create("https://meldingsformidler.digipost.no/api/ebms");
    private final Organisasjonsnummer meldingsformidlerOrganisasjon = new Organisasjonsnummer("984661185");

    private String proxyHost;
    private int proxyPort;
	private String proxyScheme = "https";
    private int maxConnectionPoolSize = 10;
    private long socketTimeoutInMillis = TimeUnit.SECONDS.toMillis(30);
    private long connectTimeoutInMillis = TimeUnit.SECONDS.toMillis(10);
    private long connectionRequestTimeoutInMillis = TimeUnit.SECONDS.toMillis(10);
    private ClientInterceptor[] soapInterceptors = new ClientInterceptor[0];
    private HttpRequestInterceptor[] httpRequestInterceptors = new HttpRequestInterceptor[0];
    private HttpResponseInterceptor[] httpResponseInterceptors = new HttpResponseInterceptor[0];

    private KlientKonfigurasjon() {}

    public URI getMeldingsformidlerRoot() {
        return meldingsformidlerRoot;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

	public String getProxyScheme() {
		return proxyScheme;
	}

    public long getSocketTimeoutInMillis() {
        return socketTimeoutInMillis;
    }

    public long getConnectTimeoutInMillis() {
        return connectTimeoutInMillis;
    }

    public long getConnectionRequestTimeoutInMillis() {
        return connectionRequestTimeoutInMillis;
    }

    public int getMaxConnectionPoolSize() {
        return maxConnectionPoolSize;
    }

    public boolean useProxy() {
        return isEmpty(proxyHost) && proxyPort > 0;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Organisasjonsnummer getMeldingsformidlerOrganisasjon() {
        return meldingsformidlerOrganisasjon;
    }

    public ClientInterceptor[] getSoapInterceptors() {
        return soapInterceptors;
    }

    public HttpRequestInterceptor[] getHttpRequestInterceptors() {
        return httpRequestInterceptors;
    }

    public HttpResponseInterceptor[] getHttpResponseInterceptors() {
        return httpResponseInterceptors;
    }

    public static class Builder {

        private final KlientKonfigurasjon target;

        private Builder() {
            target = new KlientKonfigurasjon();
        }

        public Builder meldingsformidlerRoot(final String meldingsformidlerRoot) {
            target.meldingsformidlerRoot = URI.create(meldingsformidlerRoot);
            return this;
        }

        public Builder proxy(final String proxyHost, final int proxyPort) {
            target.proxyHost = proxyHost;
            target.proxyPort = proxyPort;
            return this;
        }
        public Builder proxy(final String proxyHost, final int proxyPort, final String proxyScheme) {
            target.proxyHost = proxyHost;
            target.proxyPort = proxyPort;
            target.proxyScheme = proxyScheme;
            return this;
        }

        public Builder socketTimeout(final int socketTimeout, final TimeUnit timeUnit) {
            target.socketTimeoutInMillis = timeUnit.toMillis(socketTimeout);
            return this;
        }

        public Builder connectionTimeout(final int connectionTimeout, final TimeUnit timeUnit) {
            target.connectTimeoutInMillis = timeUnit.toMillis(connectionTimeout);
            return this;
        }

        public Builder connectionRequestTimeout(final int connectionRequestTimeout, final TimeUnit timeUnit) {
            target.connectionRequestTimeoutInMillis = timeUnit.toMillis(connectionRequestTimeout);
            return this;
        }

        public Builder maxConnectionPoolSize(final int maxConnectionPoolSize) {
            target.maxConnectionPoolSize = maxConnectionPoolSize;
            return this;
        }

        public Builder soapInterceptors(final ClientInterceptor... soapInterceptors) {
            target.soapInterceptors = soapInterceptors;
            return this;
        }

        public Builder httpRequestInterceptors(final HttpRequestInterceptor... httpRequestInterceptors) {
            target.httpRequestInterceptors = httpRequestInterceptors;
            return this;
        }

        public Builder httpResponseInterceptors(final HttpResponseInterceptor... httpResponseInterceptors) {
            target.httpResponseInterceptors = httpResponseInterceptors;
            return this;
        }

        public KlientKonfigurasjon build() {
            return target;
        }
    }

}
