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
package no.difi.sdp.client2.util;

import no.difi.sdp.client2.domain.exceptions.KonfigurasjonException;

import javax.crypto.Cipher;
import java.security.NoSuchAlgorithmException;

public class CryptoChecker {

    public static final String TRANSFORM_TO_CHECK = "AES";
    public static final int MINIMUM_SUPPORTED_KEY_LENGTH = 256;

    public static void checkCryptoPolicy() {
        try {
            int maxAesKeyLength = Cipher.getMaxAllowedKeyLength(TRANSFORM_TO_CHECK);
            if (maxAesKeyLength < MINIMUM_SUPPORTED_KEY_LENGTH) {
                throw new KonfigurasjonException("Minimum støttet nøkkellengde på platformen er for kort. " +
                        "Det skyldes sannsynligvis at du må legge inn JCE Unlimited Strength JAR. " +
                        "Merk at dette måtte gjøres både for produksjons- og utviklingsmiljøer.\n" +
                        "Maks støttet lengde er " + maxAesKeyLength + ", mens minimumskravet er " + MINIMUM_SUPPORTED_KEY_LENGTH + " for " + TRANSFORM_TO_CHECK + ".\n" +
                        "For mer informasjon, se https://www.google.no/search?q=java+cryptography+extension+unlimited+strength");
            }
        } catch (NoSuchAlgorithmException e) {
            throw new KonfigurasjonException("Klarte ikke å sjekke maks nøkkellengde for " + TRANSFORM_TO_CHECK);
        }
    }
}
