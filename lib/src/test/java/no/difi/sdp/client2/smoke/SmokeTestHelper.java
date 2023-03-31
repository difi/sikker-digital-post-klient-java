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
package no.difi.sdp.client2.smoke;

import no.difi.sdp.client2.KlientKonfigurasjon;
import no.difi.sdp.client2.SikkerDigitalPostKlient;
import no.difi.sdp.client2.domain.Databehandler;
import no.difi.sdp.client2.domain.Forsendelse;
import no.difi.sdp.client2.domain.Miljo;
import no.difi.sdp.client2.domain.Noekkelpar;
import no.difi.sdp.client2.domain.Prioritet;
import no.difi.sdp.client2.domain.kvittering.ForretningsKvittering;
import no.difi.sdp.client2.domain.kvittering.KvitteringForespoersel;
import no.difi.sdp.client2.domain.kvittering.LeveringsKvittering;
import no.digipost.api.representations.Organisasjonsnummer;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.UUID;

import static java.lang.System.out;
import static java.lang.Thread.sleep;
import static no.difi.sdp.client2.ObjectMother.TESTMILJO_VIRKSOMHETSSERTIFIKAT_ALIAS_ENVIRONMENT_VARIABLE;
import static no.difi.sdp.client2.ObjectMother.TESTMILJO_VIRKSOMHETSSERTIFIKAT_ALIAS_VALUE;
import static no.difi.sdp.client2.ObjectMother.TESTMILJO_VIRKSOMHETSSERTIFIKAT_PASSWORD_ENVIRONMENT_VARIABLE;
import static no.difi.sdp.client2.ObjectMother.TESTMILJO_VIRKSOMHETSSERTIFIKAT_PASSWORD_VALUE;
import static no.difi.sdp.client2.ObjectMother.TESTMILJO_VIRKSOMHETSSERTIFIKAT_PATH_ENVIRONMENT_VARIABLE;
import static no.difi.sdp.client2.ObjectMother.TESTMILJO_VIRKSOMHETSSERTIFIKAT_PATH_VALUE;
import static no.difi.sdp.client2.ObjectMother.databehandlerMedSertifikat;
import static no.difi.sdp.client2.ObjectMother.forsendelse;
import static no.difi.sdp.client2.ObjectMother.getVirksomhetssertifikat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.fail;

class SmokeTestHelper {

    private final String _mpcId;
    private SikkerDigitalPostKlient _klient;
    private Forsendelse _forsendelse;
    private ForretningsKvittering _forretningskvittering;

    SmokeTestHelper(Miljo miljo) {
        verifyEnvironmentVariables();
        KeyStore databehandlerCertificate = getVirksomhetssertifikat();
        Organisasjonsnummer databehanderOrgnr = getOrganisasjonsnummerFraSertifikat(databehandlerCertificate);
        _mpcId = UUID.randomUUID().toString();

        Noekkelpar databehandlerNoekkelpar = createValidDatabehandlerNoekkelparFromCertificate(databehandlerCertificate);
        Databehandler databehandler = databehandlerMedSertifikat(databehanderOrgnr, databehandlerNoekkelpar);

        KlientKonfigurasjon klientKonfigurasjon = KlientKonfigurasjon.builder(miljo).build();
        _klient = new SikkerDigitalPostKlient(databehandler, klientKonfigurasjon);
    }

    private static Noekkelpar createValidDatabehandlerNoekkelparFromCertificate(KeyStore databehandlerCertificate) {
        return Noekkelpar.fraKeyStoreUtenTrustStore(databehandlerCertificate, TESTMILJO_VIRKSOMHETSSERTIFIKAT_ALIAS_VALUE, TESTMILJO_VIRKSOMHETSSERTIFIKAT_PASSWORD_VALUE);
    }

    private static Organisasjonsnummer getOrganisasjonsnummerFraSertifikat(KeyStore keyStore) {
        try {
            X509Certificate cert = (X509Certificate) keyStore.getCertificate(TESTMILJO_VIRKSOMHETSSERTIFIKAT_ALIAS_VALUE);
            if (cert == null) {
                throw new RuntimeException(String.format("Klarte ikke hente ut virksomhetssertifikatet fra keystoren med alias '%s'", TESTMILJO_VIRKSOMHETSSERTIFIKAT_ALIAS_VALUE));
            }
            X500Name x500name = new JcaX509CertificateHolder(cert).getSubject();
            RDN serialnumber = x500name.getRDNs(BCStyle.SERIALNUMBER)[0];
            return Organisasjonsnummer.of(IETFUtils.valueToString(serialnumber.getFirst().getValue()));
        } catch (CertificateEncodingException e) {
            throw new RuntimeException("Klarte ikke hente ut organisasjonsnummer fra sertifikatet.", e);
        } catch (KeyStoreException e) {
            throw new RuntimeException("Klarte ikke hente ut virksomhetssertifikatet fra keystoren.", e);
        }
    }

    SmokeTestHelper create_digital_forsendelse() {
        assertState(_klient);

        Forsendelse forsendelse = null;
        try {
            forsendelse = forsendelse(_mpcId, new ClassPathResource("/test.pdf").getInputStream());
        } catch (IOException e) {
            fail("klarte ikke åpne hoveddokument.");
        }

        _forsendelse = forsendelse;

        return this;
    }

    SmokeTestHelper send() {
        assertState(_forsendelse);

        _klient.send(_forsendelse);

        return this;
    }

    SmokeTestHelper fetch_receipt() {
        KvitteringForespoersel kvitteringForespoersel = KvitteringForespoersel.builder(Prioritet.PRIORITERT).mpcId(_mpcId).build();
        ForretningsKvittering forretningsKvittering = null;

        try {
            sleep(2000);

            for (int i = 0; i < 10; i++) {
                forretningsKvittering = _klient.hentKvittering(kvitteringForespoersel);

                if (forretningsKvittering != null) {
                    out.println("Kvittering!");
                    out.println(String.format("%s: %s, %s, %s, %s", forretningsKvittering.getClass().getSimpleName(), forretningsKvittering.getKonversasjonsId(), forretningsKvittering.getReferanseTilMeldingId(), forretningsKvittering.getTidspunkt(), forretningsKvittering));
                    assertThat(forretningsKvittering.getKonversasjonsId(), not(emptyString()));
                    assertThat(forretningsKvittering.getReferanseTilMeldingId(), not(emptyString()));
                    assertThat(forretningsKvittering.getTidspunkt(), notNullValue());
                    assertThat(forretningsKvittering, instanceOf(LeveringsKvittering.class));

                    _klient.bekreft(forretningsKvittering);
                    break;
                } else {
                    out.println("Ingen kvittering");
                    sleep(1000);
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        _forretningskvittering = forretningsKvittering;

        return this;
    }

    SmokeTestHelper expect_receipt_to_be_leveringskvittering() {
        assertState(_forretningskvittering);

        assertThat(_forretningskvittering, instanceOf(LeveringsKvittering.class));

        return this;
    }

    SmokeTestHelper confirm_receipt() {
        _klient.bekreft(_forretningskvittering);

        return this;
    }

    private static void verifyEnvironmentVariables() {
        throwIfEnvironmentVariableNotSet("sti", TESTMILJO_VIRKSOMHETSSERTIFIKAT_PATH_VALUE);
        throwIfEnvironmentVariableNotSet("alias", TESTMILJO_VIRKSOMHETSSERTIFIKAT_ALIAS_VALUE);
        throwIfEnvironmentVariableNotSet("passord", TESTMILJO_VIRKSOMHETSSERTIFIKAT_PASSWORD_VALUE);
    }

    private static void throwIfEnvironmentVariableNotSet(String variabel, String value) {
        String oppsett = "For å kjøre smoketestene må det brukes et gyldig virksomhetssertifikat. \n" +
                "1) Sett environmentvariabel '" + TESTMILJO_VIRKSOMHETSSERTIFIKAT_PATH_ENVIRONMENT_VARIABLE + "' til full sti til virksomhetsssertifikatet. \n" +
                "2) Sett environmentvariabel '" + TESTMILJO_VIRKSOMHETSSERTIFIKAT_ALIAS_ENVIRONMENT_VARIABLE  + "' til aliaset (siste avsnitt, første del før komma): \n" +
                "       keytool -list -keystore VIRKSOMHETSSERTIFIKAT.p12 -storetype pkcs12 \n" +
                "3) Sett environmentvariabel '" + TESTMILJO_VIRKSOMHETSSERTIFIKAT_PASSWORD_ENVIRONMENT_VARIABLE + "' til passordet til virksomhetssertifikatet. \n";

        if (value == null) {
            throw new RuntimeException(String.format("Finner ikke %s til virksomhetssertifikat. \n %s", variabel, oppsett));
        }
    }

    private void assertState(Object object) {
        if (object == null) {
            throw new IllegalStateException("Requires gradually built state. Make sure you use functions in the correct order.");
        }
    }

}
