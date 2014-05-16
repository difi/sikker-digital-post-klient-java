package no.difi.sdp.client.domain.digital_post;

/**
 * Sikkerhetsnivå som beskrevet på
 */
public enum Sikkerhetsnivaa {

    /**
     * "Mellomhøyt" sikkerhetsnivå.
     *
     * Vanligvis passord.
     */
    NIVAA_3,

    /**
     * Offentlig godkjent to-faktor elektronisk ID.
     *
     * For eksempel BankID, Buypass eller Commfides.
     */
    NIVAA_4

}
