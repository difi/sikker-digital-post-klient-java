package no.difi.sdp.client2.smoke;

import no.difi.sdp.client2.domain.Miljo;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;


@Category(SmokeTest.class)
public class SmokeTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private SmokeTestHelper t;

    @Before
    public void init() {
        t = new SmokeTestHelper(Miljo.FUNKSJONELT_TESTMILJO);
    }

    @Test
    public void send_simple_digital_message() {
        t
                .create_digital_forsendelse()
                .send()
                .fetch_receipt()
                .expect_receipt_to_be_leveringskvittering()
                .confirm_receipt();
    }
}
