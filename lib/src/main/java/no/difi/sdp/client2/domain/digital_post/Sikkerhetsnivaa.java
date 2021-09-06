package no.difi.sdp.client2.domain.digital_post;

import no.difi.begrep.sdp.schema_v10.SDPSikkerhetsnivaa;

public enum Sikkerhetsnivaa {

    /**
     * "Mellomhøyt" sikkerhetsnivå.
     *
     * Vanligvis passord.
     */
    NIVAA_3(SDPSikkerhetsnivaa.NIVAA_3),

    /**
     * Offentlig godkjent to-faktor elektronisk ID.
     *
     * For eksempel BankID, Buypass eller Commfides.
     */
    NIVAA_4(SDPSikkerhetsnivaa.NIVAA_4);

    private final SDPSikkerhetsnivaa xmlValue;

    Sikkerhetsnivaa(SDPSikkerhetsnivaa xmlValue) {
        this.xmlValue = xmlValue;
    }

    public SDPSikkerhetsnivaa getXmlValue() {
        return xmlValue;
    }
}
