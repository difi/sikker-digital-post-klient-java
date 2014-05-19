package no.difi.sdp.client.domain.kvittering;

import java.util.Date;

public abstract class ForretningsKvittering {

    public ForretningsKvittering(Date tidspunkt, String konversasjonsId) {
        this.tidspunkt = tidspunkt;
        this.konversasjonsId = konversasjonsId;
    }

    private String konversasjonsId;
    private Date tidspunkt;

    public String getKonversasjonsId() {
        return konversasjonsId;
    }

    public Date getTidspunkt() {
        return tidspunkt;
    }
}
