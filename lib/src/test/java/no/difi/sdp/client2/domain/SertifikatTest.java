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
package no.difi.sdp.client2.domain;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public class SertifikatTest {

    @Test
    public void build_decode_base64_sertifikat() {
        String base64EncodedCertificate = "MIIDFDCCAr6gAwIBAgIJALENVFrUMVgcMA0GCSqGSIb3DQEBBQUAMIGQMQswCQYDVQQGEwJOTzENMAsGA1UECBMET3NsbzENMAsGA1UEBxMET3NsbzENMAsGA1UEChMERElGSTENMAsGA1UECxMERElGSTEhMB8GA1UEAxMYU2lra2VyIERpZ2l0YWwgUG9zdCBUZXN0MSIwIAYJKoZIhvcNAQkBFhNkaWZpIGF0IGRpZmkgZG90IG5vMB4XDTE0MDUxNjA4MjQ0MloXDTE0MDYxNTA4MjQ0MlowgZAxCzAJBgNVBAYTAk5PMQ0wCwYDVQQIEwRPc2xvMQ0wCwYDVQQHEwRPc2xvMQ0wCwYDVQQKEwRESUZJMQ0wCwYDVQQLEwRESUZJMSEwHwYDVQQDExhTaWtrZXIgRGlnaXRhbCBQb3N0IFRlc3QxIjAgBgkqhkiG9w0BCQEWE2RpZmkgYXQgZGlmaSBkb3Qgbm8wXDANBgkqhkiG9w0BAQEFAANLADBIAkEA1OteZ0rH+269STIDm2ECmop593A+7v9ih6ydow11wCojGvNnHGjeollzTn+F7caRqLCl7vKr3uttINBFA7E34QIDAQABo4H4MIH1MB0GA1UdDgQWBBRkkkvwgXi/qqQHLyMDttBDCN8PNzCBxQYDVR0jBIG9MIG6gBRkkkvwgXi/qqQHLyMDttBDCN8PN6GBlqSBkzCBkDELMAkGA1UEBhMCTk8xDTALBgNVBAgTBE9zbG8xDTALBgNVBAcTBE9zbG8xDTALBgNVBAoTBERJRkkxDTALBgNVBAsTBERJRkkxITAfBgNVBAMTGFNpa2tlciBEaWdpdGFsIFBvc3QgVGVzdDEiMCAGCSqGSIb3DQEJARYTZGlmaSBhdCBkaWZpIGRvdCBub4IJALENVFrUMVgcMAwGA1UdEwQFMAMBAf8wDQYJKoZIhvcNAQEFBQADQQA43EV/uoAXPEyZSXg+9g/jkxrmhNHeG8evLSM3MqLeS6lO0P6hnGlhoF9GYjqMx7ntYdE8i8jt3a5GRupTIpHB";

        Sertifikat sertifikat = Sertifikat.fraBase64X509String(base64EncodedCertificate);

        String certificateSubject = sertifikat.getX509Certificate().getSubjectX500Principal().getName();
        assertThat(certificateSubject, containsString("DIFI"));
        assertThat(certificateSubject, containsString("Sikker Digital Post Test"));
    }

}
