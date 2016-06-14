package no.difi.sdp.client2.domain.kvittering;

import no.digipost.api.representations.KanBekreftesSomBehandletKvittering;

public class LeveringsKvittering extends ForretningsKvittering {

    public LeveringsKvittering(KanBekreftesSomBehandletKvittering kanBekreftesSomBehandletKvittering, KvitteringsInfo2 kvitteringsInfo2) {
        super(kanBekreftesSomBehandletKvittering, kvitteringsInfo2);
    }

}
