package no.difi.sdp.client2.domain.kvittering;

import no.digipost.api.representations.KanBekreftesSomBehandletKvittering;

public class AapningsKvittering extends ForretningsKvittering {

    public AapningsKvittering(KanBekreftesSomBehandletKvittering kanBekreftesSomBehandletKvittering, Kvitteringsinfo kvitteringsinfo) {
        super(kanBekreftesSomBehandletKvittering, kvitteringsinfo);
    }

}
