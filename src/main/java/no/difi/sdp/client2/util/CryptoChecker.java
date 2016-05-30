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
