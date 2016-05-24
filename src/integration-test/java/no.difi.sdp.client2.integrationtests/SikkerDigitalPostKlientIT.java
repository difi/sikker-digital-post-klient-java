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
package no.difi.sdp.client2.integrationtests;

import no.difi.sdp.client2.KlientKonfigurasjon;
import no.difi.sdp.client2.SikkerDigitalPostKlient;
import no.difi.sdp.client2.domain.Forsendelse;
import no.difi.sdp.client2.domain.Noekkelpar;
import no.difi.sdp.client2.domain.Prioritet;
import no.difi.sdp.client2.domain.TekniskAvsender;
import no.difi.sdp.client2.domain.kvittering.ForretningsKvittering;
import no.difi.sdp.client2.domain.kvittering.KvitteringForespoersel;
import no.difi.sdp.client2.domain.kvittering.LeveringsKvittering;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.security.KeyStore;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SikkerDigitalPostKlientIT {

    private static SikkerDigitalPostKlient postklient;
    private static String MpcId;

    private static Noekkelpar avsenderNoekkelpar() {
        try {
            String alias = "digipost testintegrasjon for digital post";
            String passphrase = System.getenv("smoketest_passphrase") ;
            String keystorePass= "sophisticatedpassword";
            String keyStoreFile = "/SmokeTests.jceks";

            KeyStore keyStore = KeyStore.getInstance("JCEKS");
            keyStore.load(new ClassPathResource(keyStoreFile).getInputStream(), keystorePass.toCharArray());
            return Noekkelpar.fraKeyStore(keyStore, alias, passphrase);
        } catch (Exception e) {
            throw new RuntimeException("Kunne ikke laste nøkkelpar for kjøring av tester. " +
                    "For å kjøre integrasjonstester må det ligge inne et gyldig virksomhetssertifikat for test (med tilhørende certificate chain). " +
                    "Keystore med tilhørende alias og passphrase settes i  SikkerDigitalPostKlientIT.java .", e);
        }
    }

    @BeforeClass
    public static void setUp() {
        MpcId = UUID.randomUUID().toString();
        KlientKonfigurasjon klientKonfigurasjon = KlientKonfigurasjon.builder()
                .meldingsformidlerRoot("https://qaoffentlig.meldingsformidler.digipost.no/api/ebms")
                .connectionTimeout(20, TimeUnit.SECONDS)
                .build();

        TekniskAvsender avsender = ObjectMother.tekniskAvsenderMedSertifikat(avsenderNoekkelpar());

        postklient = new SikkerDigitalPostKlient(avsender, klientKonfigurasjon);
    }

    @Test
    public void A_send_digital_forsendelse() {
        Forsendelse forsendelse = null;
        try {
            forsendelse = ObjectMother.forsendelse(MpcId,new ClassPathResource("/test.pdf").getInputStream());
        } catch (IOException e) {
            fail("klarte ikke åpne hoveddokument.");
        }

        postklient.send(forsendelse);
    }


    @Test
    public void B_test_hent_kvittering() throws InterruptedException {
        KvitteringForespoersel kvitteringForespoersel = KvitteringForespoersel.builder(Prioritet.PRIORITERT).mpcId(MpcId).build();
        ForretningsKvittering forretningsKvittering = null;
        for (int i = 0; i < 10; i++) {
            forretningsKvittering = postklient.hentKvittering(kvitteringForespoersel);

            if (forretningsKvittering != null) {
                System.out.println("Kvittering!");
                System.out.println(String.format("%s: %s, %s, %s, %s", forretningsKvittering.getClass().getSimpleName(), forretningsKvittering.getKonversasjonsId(), forretningsKvittering.getRefToMessageId(), forretningsKvittering.getTidspunkt(), forretningsKvittering));
                assertThat(forretningsKvittering.getKonversasjonsId()).isNotEmpty();
                assertThat(forretningsKvittering.getRefToMessageId()).isNotEmpty();
                assertThat(forretningsKvittering.getTidspunkt()).isNotNull();
                assertThat(forretningsKvittering).isInstanceOf(LeveringsKvittering.class);

                postklient.bekreft(forretningsKvittering);
                break;
            }
            else {
                System.out.println("Ingen kvittering");
                sleep(1000);
            }
        }
        assertThat(forretningsKvittering != null).isTrue();
    }
}
