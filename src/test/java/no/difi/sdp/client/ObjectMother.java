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

import no.difi.begrep.sdp.schema_v10.SDPAapning;
import no.difi.begrep.sdp.schema_v10.SDPFeil;
import no.difi.begrep.sdp.schema_v10.SDPFeiltype;
import no.difi.begrep.sdp.schema_v10.SDPKvittering;
import no.difi.begrep.sdp.schema_v10.SDPLevering;
import no.difi.begrep.sdp.schema_v10.SDPMelding;
import no.difi.begrep.sdp.schema_v10.SDPVarslingfeilet;
import no.difi.begrep.sdp.schema_v10.SDPVarslingskanal;
import no.difi.sdp.client.domain.Behandlingsansvarlig;
import no.difi.sdp.client.domain.Dokument;
import no.difi.sdp.client.domain.Dokumentpakke;
import no.difi.sdp.client.domain.Forsendelse;
import no.difi.sdp.client.domain.Mottaker;
import no.difi.sdp.client.domain.Noekkelpar;
import no.difi.sdp.client.domain.Prioritet;
import no.difi.sdp.client.domain.Sertifikat;
import no.difi.sdp.client.domain.TekniskAvsender;
import no.difi.sdp.client.domain.digital_post.DigitalPost;
import no.difi.sdp.client.domain.digital_post.EpostVarsel;
import no.difi.sdp.client.domain.digital_post.Sikkerhetsnivaa;
import no.difi.sdp.client.domain.digital_post.SmsVarsel;
import no.digipost.api.representations.EbmsAktoer;
import no.digipost.api.representations.EbmsApplikasjonsKvittering;
import no.digipost.api.representations.Organisasjonsnummer;
import no.digipost.api.representations.StandardBusinessDocumentFactory;
import org.joda.time.DateTime;
import org.springframework.core.io.ClassPathResource;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.BusinessScope;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.DocumentIdentification;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.Partner;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.PartnerIdentification;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.Scope;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocumentHeader;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import static java.util.Arrays.asList;

public class ObjectMother {

     public static Noekkelpar noekkelpar() {
        try {
            KeyStore keyStore = KeyStore.getInstance("jks");
            keyStore.load(new ClassPathResource("/selfsigned-keystore.jks").getInputStream(), "password1234".toCharArray());
            return Noekkelpar.fraKeyStore(keyStore, "avsender", "password1234");
        } catch (Exception e) {
            throw new RuntimeException("Kunne ikke laste keystore", e);
        }
    }

    public static Forsendelse forsendelse() {
        EpostVarsel epostVarsel = EpostVarsel.builder("example@email.org", "Du har mottatt brev i din digitale postkasse")
                .varselEtterDager(asList(1, 4, 10))
                .build();

        Mottaker mottaker = Mottaker.builder("01129955131", "postkasseadresse", mottakerSertifikat(), "984661185")
                .build();

        SmsVarsel smsVarsel = SmsVarsel.builder("4799999999", "Du har mottatt brev i din digitale postkasse")
                .build();

        DigitalPost digitalPost = DigitalPost.builder(mottaker, "Ikke-sensitiv tittel for forsendelsen")
                .virkningsdato(new Date())
                .aapningskvittering(false)
                .sikkerhetsnivaa(Sikkerhetsnivaa.NIVAA_3)
                .epostVarsel(epostVarsel)
                .smsVarsel(smsVarsel)
                .build();

        Dokument hovedDokument = Dokument.builder("Sensitiv brevtittel", "faktura.pdf", new ByteArrayInputStream("hei".getBytes()))
                .mimeType("application/pdf")
                .build();

        Dokumentpakke dokumentpakke = Dokumentpakke.builder(hovedDokument)
                .vedlegg(new ArrayList<Dokument>())
                .build();

        Behandlingsansvarlig behandlingsansvarlig = behandlingsansvarlig();

        return Forsendelse.digital(behandlingsansvarlig, digitalPost, dokumentpakke)
                .konversasjonsId("konversasjonsId-" + System.currentTimeMillis())
                .prioritet(Prioritet.PRIORITERT)
                .spraakkode("NO")
                .build();
    }

    public static Behandlingsansvarlig behandlingsansvarlig() {
        return Behandlingsansvarlig.builder("991825827")
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
		return Sertifikat.fraBase64X509String("MIIE7jCCA9agAwIBAgIKGBZrmEgzTHzeJjANBgkqhkiG9w0BAQsFADBRMQswCQYD" +
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


	private static Sertifikat eboksmottakerSertifikatTest() {
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
        SDPKvittering aapningsKvittering = new SDPKvittering(null, DateTime.now(), null, new SDPAapning(), null);
        return createEbmsKvittering(aapningsKvittering);
    }

    public static EbmsApplikasjonsKvittering createEbmsLeveringsKvittering() {
        SDPKvittering leveringsKvittering = new SDPKvittering(null, DateTime.now(), null, null, new SDPLevering());
        return createEbmsKvittering(leveringsKvittering);
    }

    public static EbmsApplikasjonsKvittering createEbmsVarslingFeiletKvittering(final SDPVarslingskanal varslingskanal) {
        SDPVarslingfeilet sdpVarslingfeilet = new SDPVarslingfeilet(varslingskanal, "Varsling feilet 'Viktig brev'");
        SDPKvittering varslingFeiletKvittering = new SDPKvittering(null, DateTime.now(), sdpVarslingfeilet, null, null);
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

        return EbmsApplikasjonsKvittering.create(EbmsAktoer.avsender(avsender), EbmsAktoer.postkasse(mottaker), sbd).build();
    }
}
