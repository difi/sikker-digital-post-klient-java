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
package no.difi.sdp.client2;

import no.difi.begrep.sdp.schema_v10.SDPAapning;
import no.difi.begrep.sdp.schema_v10.SDPFeil;
import no.difi.begrep.sdp.schema_v10.SDPFeiltype;
import no.difi.begrep.sdp.schema_v10.SDPKvittering;
import no.difi.begrep.sdp.schema_v10.SDPLevering;
import no.difi.begrep.sdp.schema_v10.SDPMelding;
import no.difi.begrep.sdp.schema_v10.SDPMottak;
import no.difi.begrep.sdp.schema_v10.SDPReturpost;
import no.difi.begrep.sdp.schema_v10.SDPVarslingfeilet;
import no.difi.begrep.sdp.schema_v10.SDPVarslingskanal;
import no.difi.sdp.client2.domain.AktoerOrganisasjonsnummer;
import no.difi.sdp.client2.domain.Avsender;
import no.difi.sdp.client2.domain.AvsenderOrganisasjonsnummer;
import no.difi.sdp.client2.domain.Databehandler;
import no.difi.sdp.client2.domain.DatabehandlerOrganisasjonsnummer;
import no.difi.sdp.client2.domain.Dokument;
import no.difi.sdp.client2.domain.Dokumentpakke;
import no.difi.sdp.client2.domain.Forsendelse;
import no.difi.sdp.client2.domain.MetadataDokument;
import no.difi.sdp.client2.domain.Mottaker;
import no.difi.sdp.client2.domain.NoValidationNoekkelpar;
import no.difi.sdp.client2.domain.Noekkelpar;
import no.difi.sdp.client2.domain.Prioritet;
import no.difi.sdp.client2.domain.Sertifikat;
import no.difi.sdp.client2.domain.digital_post.DigitalPost;
import no.difi.sdp.client2.domain.digital_post.EpostVarsel;
import no.difi.sdp.client2.domain.digital_post.Sikkerhetsnivaa;
import no.difi.sdp.client2.domain.digital_post.SmsVarsel;
import no.difi.sdp.client2.internal.TrustedCertificates;
import no.digipost.api.representations.EbmsAktoer;
import no.digipost.api.representations.EbmsApplikasjonsKvittering;
import no.digipost.api.representations.Organisasjonsnummer;
import no.digipost.api.representations.StandardBusinessDocumentFactory;
import no.digipost.security.DigipostSecurity;
import org.springframework.core.io.ClassPathResource;
import no.digipost.org.unece.cefact.namespaces.standardbusinessdocumentheader.BusinessScope;
import no.digipost.org.unece.cefact.namespaces.standardbusinessdocumentheader.DocumentIdentification;
import no.digipost.org.unece.cefact.namespaces.standardbusinessdocumentheader.Partner;
import no.digipost.org.unece.cefact.namespaces.standardbusinessdocumentheader.PartnerIdentification;
import no.digipost.org.unece.cefact.namespaces.standardbusinessdocumentheader.Scope;
import no.digipost.org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;
import no.digipost.org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocumentHeader;
import no.digipost.org.w3.xmldsig.DigestMethod;
import no.digipost.org.w3.xmldsig.Reference;
import no.digipost.org.w3.xmldsig.Transform;
import no.digipost.org.w3.xmldsig.Transforms;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;

public class ObjectMother {

    public static final X509Certificate POSTEN_TEST_CERTIFICATE = DigipostSecurity.readCertificate("certificates/test/posten_test.pem");
    public static final X509Certificate POSTEN_PROD_CERTIFICATE = DigipostSecurity.readCertificate("certificates/prod/posten_prod.pem");
    public static final String SELVSIGNERT_VIRKSOMHETSSERTIFIKAT_ALIAS = "avsender";
    public static final String SELVSIGNERT_VIRKSOMHETSSERTIFIKAT_PASSORD = "password1234";
    public static final String TESTMILJO_VIRKSOMHETSSERTIFIKAT_PATH_ENVIRONMENT_VARIABLE = "no_difi_sdp_client2_virksomhetssertifikat_sti";
    public static final String TESTMILJO_VIRKSOMHETSSERTIFIKAT_ALIAS_ENVIRONMENT_VARIABLE = "no_difi_sdp_client2_virksomhetssertifikat_alias";
    public static final String TESTMILJO_VIRKSOMHETSSERTIFIKAT_PASSWORD_ENVIRONMENT_VARIABLE = "no_difi_sdp_client2_virksomhetssertifikat_passord";
    public static String TESTMILJO_VIRKSOMHETSSERTIFIKAT_PATH_VALUE = System.getenv(TESTMILJO_VIRKSOMHETSSERTIFIKAT_PATH_ENVIRONMENT_VARIABLE);
    public static String TESTMILJO_VIRKSOMHETSSERTIFIKAT_ALIAS_VALUE = System.getenv(TESTMILJO_VIRKSOMHETSSERTIFIKAT_ALIAS_ENVIRONMENT_VARIABLE);
    public static String TESTMILJO_VIRKSOMHETSSERTIFIKAT_PASSWORD_VALUE = System.getenv(TESTMILJO_VIRKSOMHETSSERTIFIKAT_PASSWORD_ENVIRONMENT_VARIABLE);

    public static Noekkelpar testEnvironmentNoekkelpar() {
        return Noekkelpar.fraKeyStoreUtenTrustStore(getVirksomhetssertifikat(), TESTMILJO_VIRKSOMHETSSERTIFIKAT_ALIAS_VALUE, TESTMILJO_VIRKSOMHETSSERTIFIKAT_PASSWORD_VALUE);
    }

    public static KeyStore getVirksomhetssertifikat() {
        KeyStore keyStore;
        try {
            keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(new FileInputStream(TESTMILJO_VIRKSOMHETSSERTIFIKAT_PATH_VALUE), TESTMILJO_VIRKSOMHETSSERTIFIKAT_PASSWORD_VALUE.toCharArray());
            return keyStore;
        } catch (Exception e) {
            throw new RuntimeException(MessageFormat.format("Fant ikke virksomhetssertifikat på sti {0}. Eksporter environmentvariabel {1} til virksomhetssertifikatet.", TESTMILJO_VIRKSOMHETSSERTIFIKAT_PATH_VALUE, TESTMILJO_VIRKSOMHETSSERTIFIKAT_PATH_ENVIRONMENT_VARIABLE), e);
        }
    }

    public static KeyStore testEnvironmentTrustStore() {
        return getKeyStore("/test-environment-trust-keystore.jceks", "sophisticatedpassword", "jceks");
    }

    private static KeyStore getKeyStore(String path, String password, String keyStoreType) {
        try {
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(new ClassPathResource(path).getInputStream(), password.toCharArray());
            return keyStore;

        } catch (Exception e) {
            throw new RuntimeException("Kunne ikke laste keystore", e);
        }
    }

    public static Forsendelse forsendelse() {
        DigitalPost digitalPost = digitalPost();

        Dokument hovedDokument = Dokument.builder("Sensitiv brevtittel", "faktura.pdf", new ByteArrayInputStream("hei".getBytes()))
                .mimeType("application/pdf")
                .metadataDocument(new MetadataDokument("lenke.xml", "application/vnd.difi.dpi.lenke+xml", "<lenke></lenke>".getBytes()))
                .build();

        Dokumentpakke dokumentpakke = Dokumentpakke.builder(hovedDokument)
                .vedlegg(new ArrayList<Dokument>())
                .build();

        Avsender avsender = avsender();

        return Forsendelse.digital(avsender, digitalPost, dokumentpakke)
                .konversasjonsId(UUID.randomUUID().toString())
                .prioritet(Prioritet.PRIORITERT)
                .spraakkode("NO")
                .build();
    }

    public static DigitalPost digitalPost() {
        String varslingsTekst = "Du har mottatt brev i din digitale postkasse";

        EpostVarsel epostVarsel = EpostVarsel.builder("example@email.org", varslingsTekst)
                .varselEtterDager(asList(1, 4, 10))
                .build();

        Mottaker mottaker = Mottaker.builder("04036125433", "ove.jonsen#6K5A", mottakerSertifikat(), Organisasjonsnummer.of("984661185"))
                .build();

        SmsVarsel smsVarsel = SmsVarsel.builder("4799999999", varslingsTekst)
                .build();

        return DigitalPost.builder(mottaker, "Ikke-sensitiv tittel for forsendelsen")
                .virkningsdato(new Date())
                .aapningskvittering(false)
                .sikkerhetsnivaa(Sikkerhetsnivaa.NIVAA_3)
                .epostVarsel(epostVarsel)
                .smsVarsel(smsVarsel)
                .build();
    }

    public static Avsender avsender() {
        return Avsender.builder(avsenderOrganisasjonsnummer()).build();
    }

    public static Sertifikat mottakerSertifikat() {
        return DigipostMottakerSertifikatTest();
    }

    public static AvsenderOrganisasjonsnummer avsenderOrganisasjonsnummer() {
        return AktoerOrganisasjonsnummer.of("988015814").forfremTilAvsender();
    }

    private static Sertifikat DigipostMottakerSertifikatTest() {
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

    public static Databehandler databehandler() {
        return Databehandler.builder(databehandlerOrganisasjonsnummer(), selvsignertNoekkelparUtenTrustStore()).build();
    }

    public static Noekkelpar selvsignertNoekkelparUtenTrustStore() {
        return new NoValidationNoekkelpar(selvsignertKeyStore(), SELVSIGNERT_VIRKSOMHETSSERTIFIKAT_ALIAS, SELVSIGNERT_VIRKSOMHETSSERTIFIKAT_PASSORD);
    }

    public static Noekkelpar selvsignertNoekkelparMedTrustStore() {
        return new NoValidationNoekkelpar(selvsignertKeyStore(), TrustedCertificates.getTrustStore(), SELVSIGNERT_VIRKSOMHETSSERTIFIKAT_ALIAS, SELVSIGNERT_VIRKSOMHETSSERTIFIKAT_PASSORD);
    }

    public static DatabehandlerOrganisasjonsnummer databehandlerOrganisasjonsnummer() {
        return AktoerOrganisasjonsnummer.of("984661185").forfremTilDatabehandler();
    }

    public static KeyStore selvsignertKeyStore() {
        return getKeyStore("/selfsigned-keystore.jks", SELVSIGNERT_VIRKSOMHETSSERTIFIKAT_PASSORD, "jks");
    }

    public static Mottaker mottaker() {
        return Mottaker.builder("01129955131", "postkasseadresse", mottakerSertifikat(), Organisasjonsnummer.of("984661185")).build();
    }

    public static EbmsApplikasjonsKvittering createEbmsFeil(final SDPFeiltype feiltype) {
        SDPFeil sdpFeil = new SDPFeil(null, ZonedDateTime.now(), feiltype, "Feilinformasjon");
        return createEbmsKvittering(sdpFeil);
    }

    public static EbmsApplikasjonsKvittering createEbmsKvittering(final Object sdpMelding) {
        Organisasjonsnummer avsenderOrganisasjonsnummer = Organisasjonsnummer.of("984661185");
        Organisasjonsnummer mottakerOrganisasjonsnummer = Organisasjonsnummer.of("988015814");

        StandardBusinessDocument sbd = new StandardBusinessDocument().withStandardBusinessDocumentHeader(
                new StandardBusinessDocumentHeader()
                        .withHeaderVersion("1.0")
                        .withSenders(new Partner().withIdentifier(new PartnerIdentification(avsenderOrganisasjonsnummer.getOrganisasjonsnummerMedLandkode(), Organisasjonsnummer.ISO6523_ACTORID)))
                        .withReceivers(new Partner().withIdentifier(new PartnerIdentification(mottakerOrganisasjonsnummer.getOrganisasjonsnummerMedLandkode(), Organisasjonsnummer.ISO6523_ACTORID)))
                        .withDocumentIdentification(new DocumentIdentification()
                                .withStandard("urn:no:difi:sdp:1.0")
                                .withTypeVersion("1.0")
                                .withInstanceIdentifier("instanceIdentifier")
                                .withType(StandardBusinessDocumentFactory.Type.from((SDPMelding) sdpMelding).toString())
                                .withCreationDateAndTime(ZonedDateTime.now())
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

        EbmsApplikasjonsKvittering build = EbmsApplikasjonsKvittering.create(EbmsAktoer.avsender(avsenderOrganisasjonsnummer), EbmsAktoer.postkasse(mottakerOrganisasjonsnummer), sbd)
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

    public static EbmsApplikasjonsKvittering createEbmsAapningsKvittering() {
        SDPKvittering aapningsKvittering = new SDPKvittering(null, ZonedDateTime.now(), null, null, new SDPAapning(), null, null);
        return createEbmsKvittering(aapningsKvittering);
    }

    public static EbmsApplikasjonsKvittering createEbmsLeveringsKvittering() {
        SDPKvittering leveringsKvittering = new SDPKvittering(null, ZonedDateTime.now(), null, null, null, new SDPLevering(), null);

        return createEbmsKvittering(leveringsKvittering);
    }

    public static EbmsApplikasjonsKvittering createEbmsMottaksKvittering() {
        SDPKvittering mottaksKvittering = new SDPKvittering(null, ZonedDateTime.now(), null, null, null, null, new SDPMottak());
        return createEbmsKvittering(mottaksKvittering);
    }

    public static EbmsApplikasjonsKvittering createEbmsReturpostKvittering() {
        SDPKvittering returpostKvittering = new SDPKvittering(null, ZonedDateTime.now(), new SDPReturpost(), null, null, null, null);
        return createEbmsKvittering(returpostKvittering);
    }

    public static EbmsApplikasjonsKvittering createEbmsVarslingFeiletKvittering(final SDPVarslingskanal varslingskanal) {
        SDPVarslingfeilet sdpVarslingfeilet = new SDPVarslingfeilet(varslingskanal, "Varsling feilet 'Viktig brev'");
        SDPKvittering varslingFeiletKvittering = new SDPKvittering(null, ZonedDateTime.now(), null, sdpVarslingfeilet, null, null, null);
        return createEbmsKvittering(varslingFeiletKvittering);
    }

    public static Dokumentpakke dokumentpakke() {
        Dokument dokument = Dokument.builder("Sensitiv tittel", "filnavn", new ByteArrayInputStream("hei".getBytes())).build();
        return Dokumentpakke.builder(dokument).build();
    }

    public static Forsendelse forsendelse(String mpcId, InputStream dokumentStream) {
        DigitalPost digitalPost = digitalPost();

        Dokument hovedDokument = Dokument.builder("Sensitiv brevtittel", "faktura.pdf", dokumentStream)
                .mimeType("application/pdf")
                .build();

        Dokumentpakke dokumentpakke = Dokumentpakke.builder(hovedDokument)
                .vedlegg(new ArrayList<Dokument>())
                .build();

        Avsender avsender = avsender();

        return Forsendelse.digital(avsender, digitalPost, dokumentpakke)
                .konversasjonsId(UUID.randomUUID().toString())
                .prioritet(Prioritet.PRIORITERT)
                .mpcId(mpcId)
                .spraakkode("NO")
                .build();
    }

    public static Databehandler databehandlerMedSertifikat(final Organisasjonsnummer organisasjonsnummer, final Noekkelpar noekkelpar) {
        return Databehandler
                .builder(AktoerOrganisasjonsnummer.of(organisasjonsnummer).forfremTilDatabehandler(), noekkelpar)
                .build();
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


}
