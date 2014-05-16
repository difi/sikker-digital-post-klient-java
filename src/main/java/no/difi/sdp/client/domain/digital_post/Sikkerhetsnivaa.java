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
    NIVAA_3(3),

    /**
     * Offentlig godkjent to-faktor elektronisk ID.
     *
     * For eksempel BankID, Buypass eller Commfides.
     */
    NIVAA_4(4);

    private Integer nivaa;

    private Sikkerhetsnivaa(Integer nivaa) {
        this.nivaa = nivaa;
    }

    public Integer getNivaa() {
        return this.nivaa;
    }
}
