package no.difi.sdp.client2;

import no.difi.begrep.sdp.schema_v10.*;
import no.difi.sdp.client2.domain.*;
import no.difi.sdp.client2.domain.digital_post.DigitalPost;
import no.difi.sdp.client2.domain.digital_post.EpostVarsel;
import no.difi.sdp.client2.domain.digital_post.Sikkerhetsnivaa;
import no.difi.sdp.client2.domain.digital_post.SmsVarsel;
import no.digipost.api.representations.EbmsAktoer;
import no.digipost.api.representations.EbmsApplikasjonsKvittering;
import no.digipost.api.representations.Organisasjonsnummer;
import no.digipost.api.representations.StandardBusinessDocumentFactory;
import org.joda.time.DateTime;
import org.springframework.core.io.ClassPathResource;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.*;
import org.w3.xmldsig.DigestMethod;
import org.w3.xmldsig.Reference;
import org.w3.xmldsig.Transform;
import org.w3.xmldsig.Transforms;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;

public class ObjectMother {

    public static final String VIRKSOMHETSSERTIFIKAT_ALIAS = "avsender";
    public static final String VIRKSOMHETSSERTIFIKAT_PASSORD = "password1234";

    public static Noekkelpar noekkelpar() {
        return Noekkelpar.fraKeyStore(selvsignertKeyStore(), VIRKSOMHETSSERTIFIKAT_ALIAS, VIRKSOMHETSSERTIFIKAT_PASSORD);
    }

    public static KeyStore selvsignertKeyStore() {
        try {
            KeyStore keyStore = KeyStore.getInstance("jks");
            keyStore.load(new ClassPathResource("/selfsigned-keystore.jks").getInputStream(), "password1234".toCharArray());
            return keyStore;

        } catch (Exception e) {
            throw new RuntimeException("Kunne ikke laste keystore", e);
        }
    }

    public static Forsendelse forsendelse() {
        DigitalPost digitalPost = digitalPost();

        Dokument hovedDokument = Dokument.builder("Sensitiv brevtittel", "faktura.pdf", new ByteArrayInputStream("hei".getBytes()))
                .mimeType("application/pdf")
                .build();

        Dokumentpakke dokumentpakke = Dokumentpakke.builder(hovedDokument)
                .vedlegg(new ArrayList<Dokument>())
                .build();

        Behandlingsansvarlig behandlingsansvarlig = behandlingsansvarlig();

        return Forsendelse.digital(behandlingsansvarlig, digitalPost, dokumentpakke)
                .konversasjonsId(UUID.randomUUID().toString())
                .prioritet(Prioritet.PRIORITERT)
                .spraakkode("NO")
                .build();
    }

    public static DigitalPost digitalPost() {
        EpostVarsel epostVarsel = EpostVarsel.builder("example@email.org", "Du har mottatt brev i din digitale postkasse")
                .varselEtterDager(asList(1, 4, 10))
                .build();

        Mottaker mottaker = Mottaker.builder("04036125433", "ove.jonsen#6K5A", mottakerSertifikat(), "984661185")
                .build();

        SmsVarsel smsVarsel = SmsVarsel.builder("4799999999", "Du har mottatt brev i din digitale postkasse")
                .build();

        return DigitalPost.builder(mottaker, "Ikke-sensitiv tittel for forsendelsen")
                .virkningsdato(new Date())
                .aapningskvittering(false)
                .sikkerhetsnivaa(Sikkerhetsnivaa.NIVAA_3)
                .epostVarsel(epostVarsel)
                .smsVarsel(smsVarsel)
                .build();
    }

    public static Behandlingsansvarlig behandlingsansvarlig() {
        return Behandlingsansvarlig.builder("984661185")
                .avsenderIdentifikator("avsenderId")
                .fakturaReferanse("ØK1")
                .build();
    }

    public static TekniskAvsender tekniskAvsender() {
        return TekniskAvsender.builder("984661185", noekkelpar())
                .build();
    }

    public static TekniskAvsender tekniskAvsenderMedSertifikat(final Noekkelpar noekkelpar) {
        return TekniskAvsender.builder("984661185", noekkelpar)
                .build();
    }

    public static Sertifikat mottakerSertifikat() {
        //return eboksmottakerSertifikatTest();
        return dpmottakerSertifikatTest();
    }

    public static Sertifikat dpmottakerSertifikatTest() {
        return Sertifikat.fraBase64X509String(
                "MIIE7jCCA9agAwIBAgIKGBZrmEgzTHzeJjANBgkqhkiG9w0BAQsFADBRMQswCQYD" +
                "VQQGEwJOTzEdMBsGA1UECgwUQnV5cGFzcyBBUy05ODMxNjMzMjcxIzAhBgNVBAMM" +
                "GkJ1eXBhc3MgQ2xhc3MgMyBUZXN0NCBDQSAzMB4XDTE0MDQyNDEyMzA1MVoXDTE3" +
                "MDQyNDIxNTkwMFowVTELMAkGA1UEBhMCTk8xGDAWBgNVBAoMD1BPU1RFTiBOT1JH" +
                "RSBBUzEYMBYGA1UEAwwPUE9TVEVOIE5PUkdFIEFTMRIwEAYDVQQFEwk5ODQ2NjEx" +
                "ODUwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCLCxU4oBhtGmJxXZWb" +
                "dWdzO2uA3eRNW/kPdddL1HYl1iXLV/g+H2Q0ELadWLggkS+1kOd8/jKxEN++biMm" +
                "mDqqCWbzNdmEd1j4lctSlH6M7tt0ywmXIYdZMz5kxcLAMNXsaqnPdikI9uPJZQEL" +
                "3Kc8hXhXISvpzP7gYOvKHg41uCxu1xCZQOM6pTlNbxemBYqvES4fRh2xvB9aMjwk" +
                "B4Nz8jrIsyoPI89i05OmGMkI5BPZt8NTa40Yf3yU+SQECW0GWalB5cxaTMeB01tq" +
                "slUzBJPV3cQx+AhtQG4hkOhQnAMDJramSPVtwbEnqOjQ+lyNmg5GQ4FJO02ApKJT" +
                "ZDTHAgMBAAGjggHCMIIBvjAJBgNVHRMEAjAAMB8GA1UdIwQYMBaAFD+u9XgLkqNw" +
                "IDVfWvr3JKBSAfBBMB0GA1UdDgQWBBQ1gsJfVC7KYGiWVLP7ZwzppyVYTTAOBgNV" +
                "HQ8BAf8EBAMCBLAwFgYDVR0gBA8wDTALBglghEIBGgEAAwIwgbsGA1UdHwSBszCB" +
                "sDA3oDWgM4YxaHR0cDovL2NybC50ZXN0NC5idXlwYXNzLm5vL2NybC9CUENsYXNz" +
                "M1Q0Q0EzLmNybDB1oHOgcYZvbGRhcDovL2xkYXAudGVzdDQuYnV5cGFzcy5uby9k" +
                "Yz1CdXlwYXNzLGRjPU5PLENOPUJ1eXBhc3MlMjBDbGFzcyUyMDMlMjBUZXN0NCUy" +
                "MENBJTIwMz9jZXJ0aWZpY2F0ZVJldm9jYXRpb25MaXN0MIGKBggrBgEFBQcBAQR+" +
                "MHwwOwYIKwYBBQUHMAGGL2h0dHA6Ly9vY3NwLnRlc3Q0LmJ1eXBhc3Mubm8vb2Nz" +
                "cC9CUENsYXNzM1Q0Q0EzMD0GCCsGAQUFBzAChjFodHRwOi8vY3J0LnRlc3Q0LmJ1" +
                "eXBhc3Mubm8vY3J0L0JQQ2xhc3MzVDRDQTMuY2VyMA0GCSqGSIb3DQEBCwUAA4IB" +
                "AQCe67UOZ/VSwcH2ov1cOSaWslL7JNfqhyNZWGpfgX1c0Gh+KkO3eVkMSozpgX6M" +
                "4eeWBWJGELMiVN1LhNaGxBU9TBMdeQ3SqK219W6DXRJ2ycBtaVwQ26V5tWKRN4Ul" +
                "RovYYiY+nMLx9VrLOD4uoP6fm9GE5Fj0vSMMPvOEXi0NsN+8MUm3HWoBeUCLyFpe" +
                "7/EPsS/Wud5bb0as/E2zIztRodxfNsoiXNvWaP2ZiPWFunIjK1H/8EcktEW1paiP" +
                "d8AZek/QQoG0MKPfPIJuqH+WJU3a8J8epMDyVfaek+4+l9XOeKwVXNSOP/JSwgpO" +
                "JNzTdaDOM+uVuk75n2191Fd7");
    }


    public static Sertifikat eboksmottakerSertifikatTest() {
        return Sertifikat.fraBase64X509String(
                "MIIE+DCCA+CgAwIBAgIKGQiM/jonpcG0VTANBgkqhkiG9w0BAQsFADBRMQswCQYD\n" +
                "VQQGEwJOTzEdMBsGA1UECgwUQnV5cGFzcyBBUy05ODMxNjMzMjcxIzAhBgNVBAMM\n" +
                "GkJ1eXBhc3MgQ2xhc3MgMyBUZXN0NCBDQSAzMB4XDTE0MDYxMjEzNTYzOFoXDTE3\n" +
                "MDYxMjIxNTkwMFowXzELMAkGA1UEBhMCTk8xEjAQBgNVBAoMCUUtQk9LUyBBUzEU\n" +
                "MBIGA1UECwwLT3BlcmF0aW9uIDExEjAQBgNVBAMMCUUtQk9LUyBBUzESMBAGA1UE\n" +
                "BRMJOTk2NDYwMzIwMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArDwI\n" +
                "/8AEOlml4abZt+zXRTxQuzuWTVx8QS2a2zE0BdUE+PO3K8QQpfPzIZVHSrhiDr03\n" +
                "VRW2zJ5qz2peGhwNw1BRBltndJLuSJBqSdfJ2TbayoBQoHJkg7YvPi11LsM2aYE7\n" +
                "5tiKN/FUqKIgqMiOz0rbTyjOcNI1cD6ZC0xskZN1ONJqG5Jxqc3NOpPTco/YA7s4\n" +
                "1v1gUPdPfoXlu5tgnmiMh4Ixwr7x7FK80aj3Akg0eWmHI8P1IxJU8hJI6sthYO0Z\n" +
                "2d8RCLeXIc4pXAkRBvgKC8I8HEYk6pDxR3UvFlwC96Mj4Ne0EN8yo3ODtT1chPp7\n" +
                "iyUPiDvNhqSRrp8GEQIDAQABo4IBwjCCAb4wCQYDVR0TBAIwADAfBgNVHSMEGDAW\n" +
                "gBQ/rvV4C5KjcCA1X1r69ySgUgHwQTAdBgNVHQ4EFgQUBL6S6KHLV/uxUDs5bB6n\n" +
                "3jZUP/4wDgYDVR0PAQH/BAQDAgSwMBYGA1UdIAQPMA0wCwYJYIRCARoBAAMCMIG7\n" +
                "BgNVHR8EgbMwgbAwN6A1oDOGMWh0dHA6Ly9jcmwudGVzdDQuYnV5cGFzcy5uby9j\n" +
                "cmwvQlBDbGFzczNUNENBMy5jcmwwdaBzoHGGb2xkYXA6Ly9sZGFwLnRlc3Q0LmJ1\n" +
                "eXBhc3Mubm8vZGM9QnV5cGFzcyxkYz1OTyxDTj1CdXlwYXNzJTIwQ2xhc3MlMjAz\n" +
                "JTIwVGVzdDQlMjBDQSUyMDM/Y2VydGlmaWNhdGVSZXZvY2F0aW9uTGlzdDCBigYI\n" +
                "KwYBBQUHAQEEfjB8MDsGCCsGAQUFBzABhi9odHRwOi8vb2NzcC50ZXN0NC5idXlw\n" +
                "YXNzLm5vL29jc3AvQlBDbGFzczNUNENBMzA9BggrBgEFBQcwAoYxaHR0cDovL2Ny\n" +
                "dC50ZXN0NC5idXlwYXNzLm5vL2NydC9CUENsYXNzM1Q0Q0EzLmNlcjANBgkqhkiG\n" +
                "9w0BAQsFAAOCAQEARj4WegvcMeqvt8R2BxB/uoNIjATmoUxlUc1f/vLkqq0fNGMt\n" +
                "RDAJWlQJ26P6Q+05G+85mK0DkRNWEjZNnX/NzMijygYwgHc0KukMoIVfYngc02Vn\n" +
                "p2QNk5YC+EGF3WjtuD9D653WkA/eKXNGEkyKPO4Okgr5akDWqUORH2ZvgyIg+r/f\n" +
                "AScTxj8YhAdooXBh5TSQqWyyCLxspY7TY/qiQ5Yk1nQTUIkrBh3UD2VSeR+ymozO\n" +
                "9DxzboFRh87BgoT0c9scVo7yWpEkMcjUdZnpvqDQ0vtKFHz/VR7JfRFWpx7JG4Cs\n" +
                "xDCnMjfCd/jSllWUjrUmKVj7es8CqXcQnjTUZg==");
    }


    public static Mottaker mottaker() {
        return Mottaker.builder("01129955131", "postkasseadresse", mottakerSertifikat(), "984661185")
                .build();
    }

    public static EbmsApplikasjonsKvittering createEbmsFeil(final SDPFeiltype feiltype) {
        SDPFeil sdpFeil = new SDPFeil(null, DateTime.now(), feiltype, "Feilinformasjon");
        return createEbmsKvittering(sdpFeil);
    }

    public static EbmsApplikasjonsKvittering createEbmsAapningsKvittering() {
        SDPKvittering aapningsKvittering = new SDPKvittering(null, DateTime.now(), null, null, new SDPAapning(), null, null);
        return createEbmsKvittering(aapningsKvittering);
    }

    public static EbmsApplikasjonsKvittering createEbmsLeveringsKvittering() {
        SDPKvittering leveringsKvittering = new SDPKvittering(null, DateTime.now(), null, null, null, new SDPLevering(), null);

        return createEbmsKvittering(leveringsKvittering);
    }

    public static EbmsApplikasjonsKvittering createEbmsMottaksKvittering() {
        SDPKvittering mottaksKvittering = new SDPKvittering(null, DateTime.now(), null, null, null, null, new SDPMottak());
        return createEbmsKvittering(mottaksKvittering);
    }

    public static EbmsApplikasjonsKvittering createEbmsReturpostKvittering() {
        SDPKvittering returpostKvittering = new SDPKvittering(null, DateTime.now(), new SDPReturpost(), null, null, null, null);
        return createEbmsKvittering(returpostKvittering);
    }

    public static EbmsApplikasjonsKvittering createEbmsVarslingFeiletKvittering(final SDPVarslingskanal varslingskanal) {
        SDPVarslingfeilet sdpVarslingfeilet = new SDPVarslingfeilet(varslingskanal, "Varsling feilet 'Viktig brev'");
        SDPKvittering varslingFeiletKvittering = new SDPKvittering(null, DateTime.now(), null, sdpVarslingfeilet, null, null, null);
        return createEbmsKvittering(varslingFeiletKvittering);
    }

    public static Dokumentpakke dokumentpakke() {
        Dokument dokument = Dokument.builder("Sensitiv tittel", "filnavn", new ByteArrayInputStream("hei".getBytes())).build();
        return Dokumentpakke.builder(dokument).build();
    }

    public static EbmsApplikasjonsKvittering createEbmsKvittering(final Object sdpMelding) {
        Organisasjonsnummer avsender = new Organisasjonsnummer("123");
        Organisasjonsnummer mottaker = new Organisasjonsnummer("456");

        StandardBusinessDocument sbd = new StandardBusinessDocument().withStandardBusinessDocumentHeader(
                new StandardBusinessDocumentHeader()
                        .withHeaderVersion("1.0")
                        .withSenders(new Partner().withIdentifier(new PartnerIdentification(avsender.asIso6523(), Organisasjonsnummer.ISO6523_ACTORID)))
                        .withReceivers(new Partner().withIdentifier(new PartnerIdentification(mottaker.asIso6523(), Organisasjonsnummer.ISO6523_ACTORID)))
                        .withDocumentIdentification(new DocumentIdentification()
                                .withStandard("urn:no:difi:sdp:1.0")
                                .withTypeVersion("1.0")
                                .withInstanceIdentifier("instanceIdentifier")
                                .withType(StandardBusinessDocumentFactory.Type.from((SDPMelding) sdpMelding).toString())
                                .withCreationDateAndTime(DateTime.now())
                        )
                        .withBusinessScope(new BusinessScope()
                                .withScopes(new Scope()
                                        .withIdentifier("urn:no:difi:sdp:1.0")
                                        .withType("ConversationId")
                                        .withInstanceIdentifier(UUID.randomUUID().toString())
                                )
                        )
        )
                .withAny(sdpMelding);

        EbmsApplikasjonsKvittering build = EbmsApplikasjonsKvittering.create(EbmsAktoer.avsender(avsender), EbmsAktoer.postkasse(mottaker), sbd)
                .withReferences(getReferences())
                .withRefToMessageId("RefToMessageId")
                .build();

        return build;
    }

    private static List<Reference> getReferences() {
        List<Reference> incomingReferences = new ArrayList<>();

        Reference reference = new Reference();
        reference.setURI("#id-f2ecf3b2-101e-433b-a30d-65a9b6779b5a");
        incomingReferences.add(reference);

        List<Transform> transforms = new ArrayList<>();
        Transform transform = new Transform();
        transform.setAlgorithm("http://www.w3.org/2001/10/xml-exc-c14n#");
        transforms.add(transform);

        DigestMethod digestMethod = new DigestMethod();
        digestMethod.setAlgorithm("http://www.w3.org/2001/04/xmlenc#sha256");
        reference.setDigestMethod(digestMethod);
        reference.setDigestValue("xQbKUtuEGSrsgZsSAT5rF+/yflr+hl2cUC4cKyiMxRM=".getBytes());

        Transforms transformsContainer = new Transforms(transforms);
        reference.setTransforms(transformsContainer);
        return incomingReferences;
    }
}
