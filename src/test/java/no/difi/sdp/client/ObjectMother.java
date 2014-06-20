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
import no.difi.sdp.client.domain.Avsender;
import no.difi.sdp.client.domain.Dokument;
import no.difi.sdp.client.domain.Dokumentpakke;
import no.difi.sdp.client.domain.Forsendelse;
import no.difi.sdp.client.domain.Mottaker;
import no.difi.sdp.client.domain.Noekkelpar;
import no.difi.sdp.client.domain.Prioritet;
import no.difi.sdp.client.domain.Sertifikat;
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

        return Forsendelse.digital(digitalPost, dokumentpakke)
                .konversasjonsId("konversasjonsId-" + System.currentTimeMillis())
                .prioritet(Prioritet.PRIORITERT)
                .spraakkode("NO")
                .build();
    }

    public static Avsender avsender() {
        return Avsender.builder("984661185", noekkelpar())
                .fakturaReferanse("ØK1")
                .avsenderIdentifikator("12345")
                .build();
    }

    public static Avsender avsenderMedSertifikat(Noekkelpar noekkelpar) {
        return Avsender.builder("984661185", noekkelpar)
                .fakturaReferanse("ØK1")
                .avsenderIdentifikator("12345")
                .build();
    }

    public static Sertifikat mottakerSertifikat() {
        return Sertifikat.fraBase64X509String("MIIDfzCCAmegAwIBAgIEAN3XETANBgkqhkiG9w0BAQsFADBwMQswCQYDVQQGEwJOTzENMAsGA1UECBMET3NsbzENMAsGA1UEBxMET3NsbzENMAsGA1UEChMERGlmaTENMAsGA1UECxMERGlmaTElMCMGA1UEAxMcU2lra2VyIERpZ2l0YWwgUG9zdCBtb3R0YWtlcjAeFw0xNDA1MjMxMTM4MjBaFw0yNDA1MjAxMTM4MjBaMHAxCzAJBgNVBAYTAk5PMQ0wCwYDVQQIEwRPc2xvMQ0wCwYDVQQHEwRPc2xvMQ0wCwYDVQQKEwREaWZpMQ0wCwYDVQQLEwREaWZpMSUwIwYDVQQDExxTaWtrZXIgRGlnaXRhbCBQb3N0IG1vdHRha2VyMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAl7FSwdLRSoHKHybxQmlUT9aX7mrqjkNKCEaRzF1w8lswEDK/j3Jmmj4I52HZgsjwobwEsGDA+828Mm+5KOgPVqhzUA7zQmVetaGzkKaE3JS8bcy4tTsrAbbf4N9lBbF6JrbCCUq25sTIkAqyXzCrNaXvtxah2K+8bOIiu8VgsHFNXest9MTxhiomx7dWk3kc/o/pb59S21+/VaM3j9oWUJ+wwkXVJTEuziN1fPYvRSoSKf+Qryx2oAAqanGYvtIBFYAMd9mgC9canMZtnEYUHXaykmLjOvR682P75hmDWNfjLbiB+uyrpzB2H+zuPX75utC40qlN/CFzwU6UtJWQxQIDAQABoyEwHzAdBgNVHQ4EFgQUeCR1OtLrQP5y4rwVJT7dnLrrc5EwDQYJKoZIhvcNAQELBQADggEBAG4sbgwgcxO2CuP2u2WGS85UXH9QOYUqU/IxvHQDgZPUlkVgn4tbouYGrBCNuWWM2F20n29dP32keDVY4s5HoF3aqwuray7zE194q/rkyqDQBaOMCiSALZU4ttKZcrsnxEYTnuVUeeU6EEEFb2wIctj2SJfvfKJ/324PwaJjln2cvxH8NSQ1py7SvFmKYhH7RobgvFzB+S8+BAoKmkBmlDECAYS1Gawixo5+e4VxiH5gqwsVEKdaR6iJzjbr/Az9muyH/pc4DSMf4V3vRaW3E8xYEdPmDydxnrsqdpFdhPF12Tk5ruoKI05ymr479tgcRxCHMt2uBO9OW+OjkMQtebU=");
    }

    public static Mottaker mottaker() {
        return Mottaker.builder("01129955131", "postkasseadresse", mottakerSertifikat(), "984661185")
                .build();
    }

    public static EbmsApplikasjonsKvittering createEbmsFeil(SDPFeiltype feiltype) {
        SDPFeil sdpFeil = new SDPFeil(null, DateTime.now(), feiltype, "Feilinformasjon");
        return createEbmsKvittering(sdpFeil);
    }

    public static EbmsApplikasjonsKvittering createEbmsAapningsKvittering() {
        SDPKvittering aapningsKvittering = new SDPKvittering(null, DateTime.now(), null, null, new SDPAapning(), null);
        return createEbmsKvittering(aapningsKvittering);
    }

    public static EbmsApplikasjonsKvittering createEbmsLeveringsKvittering() {
        SDPKvittering leveringsKvittering = new SDPKvittering(null, DateTime.now(), null, null, null, new SDPLevering());
        return createEbmsKvittering(leveringsKvittering);
    }

    public static EbmsApplikasjonsKvittering createEbmsVarslingFeiletKvittering(SDPVarslingskanal varslingskanal) {
        SDPVarslingfeilet sdpVarslingfeilet = new SDPVarslingfeilet(varslingskanal, "Varsling feilet 'Viktig brev'");
        SDPKvittering varslingFeiletKvittering = new SDPKvittering(null, DateTime.now(), null, sdpVarslingfeilet, null, null);
        return createEbmsKvittering(varslingFeiletKvittering);
    }

    public static Dokumentpakke dokumentpakke() {
        Dokument dokument = Dokument.builder("Sensitiv tittel", "filnavn", new ByteArrayInputStream("hei".getBytes())).build();
        return Dokumentpakke.builder(dokument).build();
    }

    public static EbmsApplikasjonsKvittering createEbmsKvittering(Object sdpMelding) {
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
