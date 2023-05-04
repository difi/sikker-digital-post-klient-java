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

import no.digipost.security.cert.Trust;
import org.junit.jupiter.api.Test;

import java.security.KeyStore;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class TrustedCertificatesTest {
    @Test
    public void trust_store_for_production_and_test() throws Exception {
        int numOfEnvironments = 2;
        int numOfRootCerts = 5;
        int numOfIntermediateCerts = 5;
        int numOfCertificates = numOfEnvironments * (numOfRootCerts + numOfIntermediateCerts);

        KeyStore trustStore = TrustedCertificates.getTrustStore();

        assertThat(trustStore.size(), equalTo(numOfCertificates));
    }

    @Test
    public void with_prod_certificates() {
        hasCorrectCertificateCount(Environment.PRODUCTION);
    }

    @Test
    public void with_test_certificates() {
        hasCorrectCertificateCount(Environment.TEST);
    }

    private void hasCorrectCertificateCount(Environment environment) {
        int numOfRootCerts = 5;
        int numOfIntermediateCerts = 5;
        Trust trustedCerts = TrustedCertificates.createTrust(environment);
        assertThat(trustedCerts.getTrustAnchors().size(), equalTo(numOfRootCerts));
        assertThat(trustedCerts.getTrustedIntermediateCertificates().size(), equalTo(numOfIntermediateCerts));
    }
}
