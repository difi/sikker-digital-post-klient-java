package no.difi.sdp.client.domain.kvittering;

import java.util.Date;

public abstract class ForretningsKvittering {

    protected ForretningsKvittering(Date tidspunkt, String konversasjonsId, String refToMessageId) {
        this.tidspunkt = tidspunkt;
        this.konversasjonsId = konversasjonsId;
        this.refToMessageId = refToMessageId;
    }

    private String konversasjonsId;
    private Date tidspunkt;
    private String refToMessageId;

    public String getKonversasjonsId() {
        return konversasjonsId;
    }

    public Date getTidspunkt() {
        return tidspunkt;
    }

    public String getRefToMessageId() {
        return refToMessageId;
    }
}
