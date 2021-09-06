package no.difi.sdp.client2.smoke;

import no.difi.sdp.client2.domain.Miljo;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


@Disabled("This test runs the client against a deployed backed, and thus needs correct keys set up. " +
        "Run it, and it will tell you how to set things up!")
public class SmokeTest {

    @Test
    public void send_simple_digital_message() {
        new SmokeTestHelper(Miljo.FUNKSJONELT_TESTMILJO)
                .create_digital_forsendelse()
                .send()
                .fetch_receipt()
                .expect_receipt_to_be_leveringskvittering()
                .confirm_receipt();
    }
}
