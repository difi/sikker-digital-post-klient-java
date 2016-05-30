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
package no.difi.sdp.client2.smoketest;

import no.difi.sdp.client2.KlientKonfigurasjon;
import no.difi.sdp.client2.SikkerDigitalPostKlient;
import no.difi.sdp.client2.domain.Forsendelse;
import no.difi.sdp.client2.domain.Noekkelpar;
import no.difi.sdp.client2.domain.Prioritet;
import no.difi.sdp.client2.domain.TekniskAvsender;
import no.difi.sdp.client2.domain.kvittering.ForretningsKvittering;
import no.difi.sdp.client2.domain.kvittering.KvitteringForespoersel;
import no.difi.sdp.client2.domain.kvittering.LeveringsKvittering;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;

public class SikkerDigitalPostKlientST {

    private static SikkerDigitalPostKlient sikkerDigitalPostKlient;
    private static String MpcId;
    private static String OrganizationNumber;
    private static KeyStore keyStore;

    private static final String VIRKSOMHETSSERTIFIKAT_PASSWORD_ENVIRONMENT_VARIABLE = "virksomhetssertifikat_passord";
    private static String virksomhetssertifikatPasswordValue = System.getenv(VIRKSOMHETSSERTIFIKAT_PASSWORD_ENVIRONMENT_VARIABLE);

    private static final String VIRKSOMHETSSERTIFIKAT_ALIAS_ENVIRONMENT_VARIABLE = "virksomhetssertifikat_alias";
    private static String virksomhetssertifikatAliasValue = System.getenv(VIRKSOMHETSSERTIFIKAT_ALIAS_ENVIRONMENT_VARIABLE);

    private static final String VIRKSOMHETSSERTIFIKAT_PATH_ENVIRONMENT_VARIABLE = "virksomhetssertifikat_sti";
    private static String virksomhetssertifikatPathValue = System.getenv(VIRKSOMHETSSERTIFIKAT_PATH_ENVIRONMENT_VARIABLE);

    @BeforeClass
    public static void setUp() {
        verifyEnvironmentVariables();

        keyStore = getVirksomhetssertifikat();
        MpcId = UUID.randomUUID().toString();
        OrganizationNumber = getOrganizationNumberFromCertificate();

        KlientKonfigurasjon klientKonfigurasjon = KlientKonfigurasjon.builder()
                .meldingsformidlerRoot("https://qaoffentlig.meldingsformidler.digipost.no/api/ebms")
                .connectionTimeout(20, TimeUnit.SECONDS)
                .build();

        TekniskAvsender avsender = ObjectMother.tekniskAvsenderMedSertifikat(OrganizationNumber, avsenderNoekkelpar());

        sikkerDigitalPostKlient = new SikkerDigitalPostKlient(avsender, klientKonfigurasjon);
    }

    private static void verifyEnvironmentVariables() {
        throwEnvironmentVariabelIkkeSatt("sti", virksomhetssertifikatPathValue);
        throwEnvironmentVariabelIkkeSatt("alias", virksomhetssertifikatAliasValue);
        throwEnvironmentVariabelIkkeSatt("passord", virksomhetssertifikatPasswordValue);
    }

    private static void throwEnvironmentVariabelIkkeSatt(String variabel, String value) {
        String oppsett = "For å kjøre smoketestene må det brukes et gyldig virksomhetssertifikat. \n" +
                "1) Sett environmentvariabel '" + VIRKSOMHETSSERTIFIKAT_PATH_ENVIRONMENT_VARIABLE + "' til full sti til virksomhetsssertifikatet. \n" +
                "2) Sett environmentvariabel '" + VIRKSOMHETSSERTIFIKAT_ALIAS_ENVIRONMENT_VARIABLE + "' til aliaset (siste avsnitt, første del før komma): \n" +
                "       keytool -list -keystore VIRKSOMHETSSERTIFIKAT.p12 -storetype pkcs12 \n" +
                "3) Sett environmentvariabel '" + VIRKSOMHETSSERTIFIKAT_PASSWORD_ENVIRONMENT_VARIABLE + "' til passordet til virksomhetssertifikatet. \n";

        if (value == null) {
            throw new RuntimeException(String.format("Finner ikke %s til virksomhetssertifikat. \n %s", variabel, oppsett));
        }
    }

    private static Noekkelpar avsenderNoekkelpar() {
        return Noekkelpar.fraKeyStoreUtenTrustStore(keyStore, virksomhetssertifikatAliasValue, virksomhetssertifikatPasswordValue);
    }

    private static String getOrganizationNumberFromCertificate() {
        try {
            X509Certificate cert = (X509Certificate) keyStore.getCertificate(virksomhetssertifikatAliasValue);
            if (cert == null) {
                throw new RuntimeException(String.format("Klarte ikke hente ut virksomhetssertifikatet fra keystoren med alias '%s'", virksomhetssertifikatAliasValue));
            }
            X500Name x500name = new JcaX509CertificateHolder(cert).getSubject();
            RDN serialnumber = x500name.getRDNs(BCStyle.SN)[0];
            return IETFUtils.valueToString(serialnumber.getFirst().getValue());
        } catch (CertificateEncodingException e) {
            throw new RuntimeException("Klarte ikke hente ut organisasjonsnummer fra sertifikatet.", e);
        } catch (KeyStoreException e) {
            throw new RuntimeException("Klarte ikke hente ut virksomhetssertifikatet fra keystoren.", e);
        }
    }

    private static KeyStore getVirksomhetssertifikat() {

        try {
            keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(new FileInputStream(virksomhetssertifikatPathValue), virksomhetssertifikatPasswordValue.toCharArray());
            return keyStore;
        } catch (Exception e) {
            throw new RuntimeException("Kunne ikke initiere keystoren. Legg virksomhetssertifikatet.p12 i src/smoke-test/resources/. ", e);
        }
    }

    @Test
    public void send_digital_forsendelse_og_hent_kvittering() throws InterruptedException {
        Forsendelse forsendelse = null;
        try {
            forsendelse = ObjectMother.forsendelse(OrganizationNumber, MpcId, new ClassPathResource("/test.pdf").getInputStream());
        } catch (IOException e) {
            fail("klarte ikke åpne hoveddokument.");
        }

        sikkerDigitalPostKlient.send(forsendelse);

        KvitteringForespoersel kvitteringForespoersel = KvitteringForespoersel.builder(Prioritet.PRIORITERT).mpcId(MpcId).build();
        ForretningsKvittering forretningsKvittering = null;
        sleep(1000);//wait 1 sec until first try.
        for (int i = 0; i < 10; i++) {
            forretningsKvittering = sikkerDigitalPostKlient.hentKvittering(kvitteringForespoersel);

            if (forretningsKvittering != null) {
                System.out.println("Kvittering!");
                System.out.println(String.format("%s: %s, %s, %s, %s", forretningsKvittering.getClass().getSimpleName(), forretningsKvittering.getKonversasjonsId(), forretningsKvittering.getRefToMessageId(), forretningsKvittering.getTidspunkt(), forretningsKvittering));
                assertThat(forretningsKvittering.getKonversasjonsId()).isNotEmpty();
                assertThat(forretningsKvittering.getRefToMessageId()).isNotEmpty();
                assertThat(forretningsKvittering.getTidspunkt()).isNotNull();
                assertThat(forretningsKvittering).isInstanceOf(LeveringsKvittering.class);

                sikkerDigitalPostKlient.bekreft(forretningsKvittering);
                break;
            } else {
                System.out.println("Ingen kvittering");
                sleep(1000);
            }
        }
        assertThat(forretningsKvittering != null).isTrue();
    }
}
