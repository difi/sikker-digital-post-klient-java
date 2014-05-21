package no.difi.sdp.client;

import no.difi.sdp.client.domain.Avsender;
import no.difi.sdp.client.domain.Noekkelpar;
import org.springframework.core.io.ClassPathResource;

import java.security.KeyStore;

public class ObjectMother {

    public static Noekkelpar noekkelpar() {
        try {
            KeyStore keyStore = KeyStore.getInstance("jks");
            keyStore.load(new ClassPathResource("/avsender-keystore.jks").getInputStream(), "password1234".toCharArray());
            return Noekkelpar.fraKeyStore(keyStore, "avsender", "password1234");
        } catch (Exception e) {
            throw new RuntimeException("Kunne ikke laste keystore", e);
        }
    }

    public static Avsender avsender() {
        return Avsender.builder("984661185", noekkelpar())
                .fakturaReferanse("ØK1")
                .avsenderIdentifikator("12345")
                .build();

    }


}
