package no.difi.sdp.client2.domain.kvittering;

import no.digipost.api.representations.KanBekreftesSomBehandletKvittering;

public class ReturpostKvittering extends ForretningsKvittering {

	public ReturpostKvittering(KanBekreftesSomBehandletKvittering kanBekreftesSomBehandletKvittering, KvitteringsInfo2 kvitteringsInfo2) {
		super(kanBekreftesSomBehandletKvittering, kvitteringsInfo2);
	}

}
