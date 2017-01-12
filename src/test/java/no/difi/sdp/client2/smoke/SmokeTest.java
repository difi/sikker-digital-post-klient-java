package no.difi.sdp.client2.smoke;

import no.difi.sdp.client2.KlientKonfigurasjon;
import no.difi.sdp.client2.ObjectMother;
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

import static java.lang.System.out;
import static java.lang.Thread.sleep;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;


@Category(SmokeTest.class)
public class SmokeTest {

    @Test
    public void do_send_regular(){
        SmokeTestHelper t = new SmokeTestHelper(Miljo.FUNKSJONELT_TESTMILJO);

        t
                .create_digital_forsendelse()
                .send()
                .fetch_receipt()
                .expect_receipt_to_be_leveringskvittering()
                .confirm_receipt();
    }
}
