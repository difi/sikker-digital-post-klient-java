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
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.UUID;

import static java.lang.System.out;
import static java.lang.Thread.sleep;
import static no.difi.sdp.client2.ObjectMother.*;
import static no.difi.sdp.client2.ObjectMother.getVirksomhetssertifikat;
import static no.difi.sdp.client2.ObjectMother.TESTMILJO_VIRKSOMHETSSERTIFIKAT_PASSWORD_VALUE;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

class SmokeTestHelper {

    private Miljo _miljo;
    private final KeyStore _databehandlerCertificate;
    private final Organisasjonsnummer _databehanderOrgnr;

    private SikkerDigitalPostKlient _klient;
    private final String _mpcId;
    private Forsendelse _forsendelse;
    private ForretningsKvittering _forretningskvittering;

    SmokeTestHelper(Miljo miljo) {
        _miljo = miljo;
        _databehandlerCertificate = getVirksomhetssertifikat();
        _databehanderOrgnr = getOrganisasjonsnummerFraSertifikat(_databehandlerCertificate);
        _mpcId = UUID.randomUUID().toString();
    }

    SmokeTestHelper with_valid_noekkelpar_for_databehandler() {
        Noekkelpar databehandlerNoekkelpar = createValidDatabehandlerNoekkelparFromCertificate(_databehandlerCertificate);
        Databehandler databehandler = databehandlerMedSertifikat(_databehanderOrgnr, databehandlerNoekkelpar);

        KlientKonfigurasjon klientKonfigurasjon = KlientKonfigurasjon.builder(_miljo).build();
        _klient = new SikkerDigitalPostKlient(databehandler, klientKonfigurasjon);

        return this;
    }

    private static Noekkelpar createValidDatabehandlerNoekkelparFromCertificate(KeyStore databehandlerCertificate) {
        return Noekkelpar.fraKeyStoreUtenTrustStore(databehandlerCertificate, TESTMILJO_VIRKSOMHETSSERTIFIKAT_ALIAS_VALUE, TESTMILJO_VIRKSOMHETSSERTIFIKAT_PASSWORD_VALUE);
    }

    private static Noekkelpar createInvalidDatabehandlerNoekkelparFromCertificate(KeyStore databehandlerCertificate) {
        return Noekkelpar.fraKeyStore(databehandlerCertificate, TESTMILJO_VIRKSOMHETSSERTIFIKAT_ALIAS_VALUE, TESTMILJO_VIRKSOMHETSSERTIFIKAT_PASSWORD_VALUE);
    }

    SmokeTestHelper with_invalid_noekkelpar_for_databehandler() {
        Noekkelpar databehandlerNoekkelpar = createInvalidDatabehandlerNoekkelparFromCertificate(_databehandlerCertificate);
        Databehandler databehandler = databehandlerMedSertifikat(_databehanderOrgnr, databehandlerNoekkelpar);

        KlientKonfigurasjon klientKonfigurasjon = KlientKonfigurasjon.builder(_miljo).build();
        _klient = new SikkerDigitalPostKlient(databehandler, klientKonfigurasjon);

        return this;
    }

    private static Organisasjonsnummer getOrganisasjonsnummerFraSertifikat(KeyStore keyStore) {
        try {
            X509Certificate cert = (X509Certificate) keyStore.getCertificate(TESTMILJO_VIRKSOMHETSSERTIFIKAT_ALIAS_VALUE);
            if (cert == null) {
                throw new RuntimeException(String.format("Klarte ikke hente ut virksomhetssertifikatet fra keystoren med alias '%s'", TESTMILJO_VIRKSOMHETSSERTIFIKAT_ALIAS_VALUE));
            }
            X500Name x500name = new JcaX509CertificateHolder(cert).getSubject();
            RDN serialnumber = x500name.getRDNs(BCStyle.SN)[0];
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
                    assertThat(forretningsKvittering.getKonversasjonsId(), not(isEmptyString()));
                    assertThat(forretningsKvittering.getReferanseTilMeldingId(), not(isEmptyString()));
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

        Assert.assertThat(_forretningskvittering, Matchers.instanceOf(LeveringsKvittering.class));

        return this;
    }

    SmokeTestHelper confirm_receipt() {
        _klient.bekreft(_forretningskvittering);

        return this;
    }

    private void assertState(Object object) {
        if (object == null) {
            throw new IllegalStateException("Requires gradually built state. Make sure you use functions in the correct order.");
        }
    }

}
