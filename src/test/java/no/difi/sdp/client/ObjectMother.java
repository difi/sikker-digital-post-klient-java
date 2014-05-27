package no.difi.sdp.client;

import no.difi.sdp.client.domain.Avsender;
import no.difi.sdp.client.domain.Noekkelpar;
import no.difi.sdp.client.domain.Sertifikat;
import org.springframework.core.io.ClassPathResource;

import java.security.KeyStore;

public class ObjectMother {

    public static Noekkelpar noekkelparMF() {
        try {
            KeyStore keyStore = KeyStore.getInstance("JCEKS");
            keyStore.load(new ClassPathResource("/meldingsformidler.qa.jce").getInputStream(), "abcd1234".toCharArray());
            return Noekkelpar.fraKeyStore(keyStore, "meldingsformidler", "abcd1234");
        } catch (Exception e) {
            throw new RuntimeException("Kunne ikke laste keystore", e);
        }
    }

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

    public static Avsender avsenderMedBuypassSertifikat() {
        return Avsender.builder("984661185", noekkelparMF())
                .fakturaReferanse("ØK1")
                .avsenderIdentifikator("12345")
                .build();
    }

    public static Sertifikat mottakerSertifikat() {
        return Sertifikat.fraBase64X509String("MIIDfzCCAmegAwIBAgIEAN3XETANBgkqhkiG9w0BAQsFADBwMQswCQYDVQQGEwJOTzENMAsGA1UECBMET3NsbzENMAsGA1UEBxMET3NsbzENMAsGA1UEChMERGlmaTENMAsGA1UECxMERGlmaTElMCMGA1UEAxMcU2lra2VyIERpZ2l0YWwgUG9zdCBtb3R0YWtlcjAeFw0xNDA1MjMxMTM4MjBaFw0yNDA1MjAxMTM4MjBaMHAxCzAJBgNVBAYTAk5PMQ0wCwYDVQQIEwRPc2xvMQ0wCwYDVQQHEwRPc2xvMQ0wCwYDVQQKEwREaWZpMQ0wCwYDVQQLEwREaWZpMSUwIwYDVQQDExxTaWtrZXIgRGlnaXRhbCBQb3N0IG1vdHRha2VyMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAl7FSwdLRSoHKHybxQmlUT9aX7mrqjkNKCEaRzF1w8lswEDK/j3Jmmj4I52HZgsjwobwEsGDA+828Mm+5KOgPVqhzUA7zQmVetaGzkKaE3JS8bcy4tTsrAbbf4N9lBbF6JrbCCUq25sTIkAqyXzCrNaXvtxah2K+8bOIiu8VgsHFNXest9MTxhiomx7dWk3kc/o/pb59S21+/VaM3j9oWUJ+wwkXVJTEuziN1fPYvRSoSKf+Qryx2oAAqanGYvtIBFYAMd9mgC9canMZtnEYUHXaykmLjOvR682P75hmDWNfjLbiB+uyrpzB2H+zuPX75utC40qlN/CFzwU6UtJWQxQIDAQABoyEwHzAdBgNVHQ4EFgQUeCR1OtLrQP5y4rwVJT7dnLrrc5EwDQYJKoZIhvcNAQELBQADggEBAG4sbgwgcxO2CuP2u2WGS85UXH9QOYUqU/IxvHQDgZPUlkVgn4tbouYGrBCNuWWM2F20n29dP32keDVY4s5HoF3aqwuray7zE194q/rkyqDQBaOMCiSALZU4ttKZcrsnxEYTnuVUeeU6EEEFb2wIctj2SJfvfKJ/324PwaJjln2cvxH8NSQ1py7SvFmKYhH7RobgvFzB+S8+BAoKmkBmlDECAYS1Gawixo5+e4VxiH5gqwsVEKdaR6iJzjbr/Az9muyH/pc4DSMf4V3vRaW3E8xYEdPmDydxnrsqdpFdhPF12Tk5ruoKI05ymr479tgcRxCHMt2uBO9OW+OjkMQtebU=");
    }


}
