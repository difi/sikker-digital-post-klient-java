package no.difi.sdp.client.domain.digital_post;

import org.junit.Test;

public class VarselTest {

    @Test(expected = IllegalArgumentException.class)
    public void test_kan_ikke__smsVarsel_uten_mobilnummer() {
        SmsVarsel.builder(null, "Du har mottatt digital post").build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_kan_ikke__smsVarsel_uten_varslingsTekst() {
        SmsVarsel.builder("12345678", "").build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_kan_ikke_nullstille_repetisjoner() {
        SmsVarsel.builder("12345678", "Du har mottatt digital post").varselEtterDager(null).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_kan_ikke_epostVarsel_uten_epostadresse() {
        EpostVarsel.builder(null, "Du har mottatt digital post").build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_kan_ikke__epostVarsel_uten_varslingsTekst() {
        SmsVarsel.builder("test@test.no", "").build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_kan_ikke_nullstille_epostVarsel_repetisjoner() {
        EpostVarsel.builder("test@test.no", "Du har mottatt digital post").varselEtterDager(null).build();
    }
}
