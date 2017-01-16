package no.difi.sdp.client2.smoke;

import no.difi.sdp.client2.domain.Miljo;
import no.difi.sdp.client2.domain.exceptions.NoekkelException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(SmokeTest.class)
public class SmokeTest {

    private SmokeTestHelper t;

    @Before
    public void init() {
        t = new SmokeTestHelper(Miljo.FUNKSJONELT_TESTMILJO);
    }


    @Test
    public void send_simple_digital_message() {
        t
                .with_valid_noekkelpar_for_databehandler()
                .create_digital_forsendelse()
                .send()
                .fetch_receipt()
                .expect_receipt_to_be_leveringskvittering()
                .confirm_receipt();
    }

    @Test
    public void send_with_missing_trust_store_fails_with_correct_exception() {
        try {
            t
                    .with_invalid_noekkelpar_for_databehandler()
                    .create_digital_forsendelse()
                    .send();
        } catch (Exception e) {
            Assert.assertThat(e.getCause(), Matchers.instanceOf(NoekkelException.class));
        }
    }
}
