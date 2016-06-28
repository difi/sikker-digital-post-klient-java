package no.difi.sdp.client2.smoke;

import no.difi.sdp.client2.KlientKonfigurasjon;
import no.difi.sdp.client2.ObjectMother;
import no.difi.sdp.client2.SikkerDigitalPostKlient;
import no.difi.sdp.client2.domain.Forsendelse;
import no.difi.sdp.client2.domain.Noekkelpar;
import no.difi.sdp.client2.domain.Prioritet;
import no.difi.sdp.client2.domain.Databehandler;
import no.difi.sdp.client2.domain.kvittering.ForretningsKvittering;
import no.difi.sdp.client2.domain.kvittering.KvitteringForespoersel;
import no.difi.sdp.client2.domain.kvittering.LeveringsKvittering;
import no.digipost.api.representations.Organisasjonsnummer;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
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

@Category(SmokeTest.class)
public class SmokeTest {

    private static SikkerDigitalPostKlient sikkerDigitalPostKlient;
    private static String organizationNumberFromCertificate;
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
        organizationNumberFromCertificate = getOrganizationNumberFromCertificate();

        KlientKonfigurasjon klientKonfigurasjon = KlientKonfigurasjon
                .builder("https://qaoffentlig.meldingsformidler.digipost.no/api/ebms")
                .connectionTimeout(20, TimeUnit.SECONDS)
                .build();

        Databehandler databehandler = ObjectMother.databehandlerMedSertifikat(Organisasjonsnummer.of("984661185"), avsenderNoekkelpar());

        sikkerDigitalPostKlient = new SikkerDigitalPostKlient(databehandler, klientKonfigurasjon);
    }

    private static void verifyEnvironmentVariables() {
        throwIfEnvironmentVariableNotSet("sti", virksomhetssertifikatPathValue);
        throwIfEnvironmentVariableNotSet("alias", virksomhetssertifikatAliasValue);
        throwIfEnvironmentVariableNotSet("passord", virksomhetssertifikatPasswordValue);
    }

    private static void throwIfEnvironmentVariableNotSet(String variabel, String value) {
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
            throw new RuntimeException(String.format("Fant ikke virksomhetssertifikat på sti '%s'. Eksporter environmentvariabel '%s' til virksomhetssertifikatet.", virksomhetssertifikatPathValue, VIRKSOMHETSSERTIFIKAT_PATH_ENVIRONMENT_VARIABLE), e);
        }
    }

    @Test
    public void send_digital_forsendelse_og_hent_kvittering() throws InterruptedException {
        String mpcId = UUID.randomUUID().toString();

        Forsendelse forsendelse = buildForsendelse(mpcId);

        sikkerDigitalPostKlient.send(forsendelse);
        ForretningsKvittering forretningsKvittering = getForretningsKvittering(sikkerDigitalPostKlient, mpcId);
        sikkerDigitalPostKlient.bekreft(forretningsKvittering);

        assertThat(forretningsKvittering != null).isTrue();
    }

    private Forsendelse buildForsendelse(String mpcId) {
        Forsendelse forsendelse = null;
        try {
            forsendelse = ObjectMother.forsendelse(mpcId, new ClassPathResource("/test.pdf").getInputStream());
        } catch (IOException e) {
            fail("klarte ikke åpne hoveddokument.");
        }
        return forsendelse;
    }

    private ForretningsKvittering getForretningsKvittering(SikkerDigitalPostKlient sikkerDigitalPostKlient, String mpcId) throws InterruptedException {
        KvitteringForespoersel kvitteringForespoersel = KvitteringForespoersel.builder(Prioritet.PRIORITERT).mpcId(mpcId).build();
        ForretningsKvittering forretningsKvittering = null;
        sleep(2000);
        for (int i = 0; i < 10; i++) {
            forretningsKvittering = sikkerDigitalPostKlient.hentKvittering(kvitteringForespoersel);

            if (forretningsKvittering != null) {
                System.out.println("Kvittering!");
                System.out.println(String.format("%s: %s, %s, %s, %s", forretningsKvittering.getClass().getSimpleName(), forretningsKvittering.kvitteringsInfo.getKonversasjonsId(), forretningsKvittering.kvitteringsInfo.getReferanseTilMeldingId(), forretningsKvittering.kvitteringsInfo.getTidspunkt(), forretningsKvittering));
                assertThat(forretningsKvittering.kvitteringsInfo.getKonversasjonsId()).isNotEmpty();
                assertThat(forretningsKvittering.kvitteringsInfo.getReferanseTilMeldingId()).isNotEmpty();
                assertThat(forretningsKvittering.kvitteringsInfo.getTidspunkt()).isNotNull();
                assertThat(forretningsKvittering).isInstanceOf(LeveringsKvittering.class);

                sikkerDigitalPostKlient.bekreft(forretningsKvittering);
                break;
            } else {
                System.out.println("Ingen kvittering");
                sleep(1000);
            }
        }
        return forretningsKvittering;
    }
}
