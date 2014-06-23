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

import no.difi.sdp.client.domain.TekniskAvsender;
import no.difi.sdp.client.domain.Forsendelse;
import no.difi.sdp.client.domain.Noekkelpar;
import no.difi.sdp.client.domain.Prioritet;
import no.difi.sdp.client.domain.kvittering.AapningsKvittering;
import no.difi.sdp.client.domain.kvittering.ForretningsKvittering;
import no.difi.sdp.client.domain.kvittering.KvitteringForespoersel;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.security.KeyStore;
import java.util.concurrent.TimeUnit;

import static no.difi.sdp.client.ObjectMother.createEbmsAapningsKvittering;
import static no.difi.sdp.client.ObjectMother.forsendelse;
import static org.fest.assertions.api.Assertions.assertThat;

public class SikkerDigitalPostKlientIntegrationTest {

    private SikkerDigitalPostKlient postklient;

    private Noekkelpar avsenderNoekkelpar() {
        try {
            String alias = "meldingsformidler";
            String passphrase = "abcd1234";
            String keyStoreFile = "/keystore.jce";

            KeyStore keyStore = KeyStore.getInstance("JCEKS");
            keyStore.load(new ClassPathResource(keyStoreFile).getInputStream(), passphrase.toCharArray());
            return Noekkelpar.fraKeyStore(keyStore, alias, passphrase);
        } catch (Exception e) {
            throw new RuntimeException("Kunne ikke laste nøkkelpar for kjøring av tester. " +
                    "For å kjøre integrasjonstester må det ligge inne et gyldig virksomhetssertifikat for test (med tilhørende certificate chain). " +
                    "Keystore med tilhørende alias og passphrase settes i " + this.getClass().getSimpleName() + ".", e);
        }
    }

    @Before
    public void setUp() {
        KlientKonfigurasjon klientKonfigurasjon = KlientKonfigurasjon.builder()
                .meldingsformidlerRoot("https://qaoffentlig.meldingsformidler.digipost.no/api/ebms")
                .connectionTimeout(20, TimeUnit.SECONDS)
                .build();

        TekniskAvsender avsender = ObjectMother.tekniskAvsenderMedSertifikat(avsenderNoekkelpar());

        postklient = new SikkerDigitalPostKlient(avsender, klientKonfigurasjon);
    }

    @Test
    @Ignore
    public void send_digital_forsendelse() {
        Forsendelse forsendelse = forsendelse();

        postklient.send(forsendelse);
    }

    @Test
    @Ignore
    public void test_hent_kvittering() {
        KvitteringForespoersel kvitteringForespoersel = KvitteringForespoersel.builder(Prioritet.NORMAL).build();

        for (int i = 0; i < 10; i++) {
            ForretningsKvittering forretningsKvittering = postklient.hentKvittering(kvitteringForespoersel);

            if (forretningsKvittering != null) {
                System.out.println("Kvittering!");
                System.out.println(String.format("%s: %s, %s, %s, %s", forretningsKvittering.getClass().getSimpleName(), forretningsKvittering.getKonversasjonsId(), forretningsKvittering.getRefToMessageId(), forretningsKvittering.getTidspunkt(), forretningsKvittering));
                assertThat(forretningsKvittering.getKonversasjonsId()).isNotEmpty();
                assertThat(forretningsKvittering.getRefToMessageId()).isNotEmpty();
                assertThat(forretningsKvittering.getTidspunkt()).isNotNull();
            }
            else {
                System.out.println("Ingen kvittering");
                break;
            }
        }
    }

    @Test
    @Ignore
    public void test_hent_kvittering_og_bekreft_forrige() {
        KvitteringForespoersel kvitteringForespoersel = KvitteringForespoersel.builder(Prioritet.NORMAL).build();
        ForretningsKvittering forrigeKvittering = AapningsKvittering.builder(createEbmsAapningsKvittering()).build();

        ForretningsKvittering forretningsKvittering = postklient.hentKvitteringOgBekreftForrige(kvitteringForespoersel, forrigeKvittering);
        if (forretningsKvittering != null) {
            assertThat(forretningsKvittering.getKonversasjonsId()).isNotEmpty();
            assertThat(forretningsKvittering.getMessageId()).isNotEmpty();
            assertThat(forretningsKvittering.getRefToMessageId()).isNotEmpty();
            assertThat(forretningsKvittering.getTidspunkt()).isNotNull();
        }
    }

    @Test
    @Ignore
    public void test_bekreft_kvittering() {
        ForretningsKvittering forrigeKvittering = AapningsKvittering.builder(createEbmsAapningsKvittering()).build();
        postklient.bekreft(forrigeKvittering);
    }

}
