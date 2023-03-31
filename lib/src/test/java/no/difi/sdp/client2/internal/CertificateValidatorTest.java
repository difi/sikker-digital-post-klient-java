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

import no.difi.sdp.client2.domain.Miljo;
import no.difi.sdp.client2.domain.exceptions.SertifikatException;
import no.digipost.security.cert.Trust;
import org.junit.jupiter.api.Test;

import static no.difi.sdp.client2.ObjectMother.POSTEN_PROD_CERTIFICATE;
import static no.difi.sdp.client2.ObjectMother.POSTEN_TEST_CERTIFICATE;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CertificateValidatorTest {

    @Test
    public void accepts_test_certificate() {
        CertificateValidator.validate(Miljo.FUNKSJONELT_TESTMILJO, POSTEN_TEST_CERTIFICATE);
    }

    @Test
    public void accepts_prod_certificate() {
        CertificateValidator.validate(Miljo.PRODUKSJON, POSTEN_PROD_CERTIFICATE);
    }

    @Test
    public void stops_test_certificate_in_prod() {
        assertThrows(SertifikatException.class, () -> CertificateValidator.validate(Miljo.PRODUKSJON, POSTEN_TEST_CERTIFICATE));
    }

    @Test
    public void stops_prod_certificate_in_test() {
        assertThrows(SertifikatException.class, () -> CertificateValidator.validate(Miljo.FUNKSJONELT_TESTMILJO, POSTEN_PROD_CERTIFICATE));
    }

    @Test
    public void no_validation_if_trusted_chain_certificates_are_null() {
        Miljo miljo = Miljo.FUNKSJONELT_TESTMILJO;
        Trust tmpGodkjenteSertifikater = miljo.getGodkjenteKjedeSertifikater();
        miljo.setGodkjenteKjedeSertifikater(null);

        CertificateValidator.validate(miljo, POSTEN_PROD_CERTIFICATE);

        miljo.setGodkjenteKjedeSertifikater(tmpGodkjenteSertifikater);
    }
}