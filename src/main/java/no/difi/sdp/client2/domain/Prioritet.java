package no.difi.sdp.client2.domain;

import no.digipost.api.representations.EbmsOutgoingMessage;

public enum Prioritet {

    NORMAL(EbmsOutgoingMessage.Prioritet.NORMAL),
    PRIORITERT(EbmsOutgoingMessage.Prioritet.PRIORITERT);

    private final EbmsOutgoingMessage.Prioritet ebmsPrioritet;

    Prioritet(EbmsOutgoingMessage.Prioritet ebmsPrioritet) {
        this.ebmsPrioritet = ebmsPrioritet;
    }

    public EbmsOutgoingMessage.Prioritet getEbmsPrioritet() {
        return ebmsPrioritet;
    }
}
