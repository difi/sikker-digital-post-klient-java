package no.difi.sdp.client2.domain.kvittering;

import no.digipost.api.representations.KanBekreftesSomBehandletKvittering;

public class MottaksKvittering extends ForretningsKvittering {

    public MottaksKvittering(KanBekreftesSomBehandletKvittering kanBekreftesSomBehandletKvittering, KvitteringsInfo2 kvitteringsInfo2) {
        super(kanBekreftesSomBehandletKvittering, kvitteringsInfo2);
    }

}
