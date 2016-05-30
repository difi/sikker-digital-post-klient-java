package no.difi.sdp.client2.domain.kvittering;

import no.digipost.api.representations.EbmsApplikasjonsKvittering;

public class ReturpostKvittering extends ForretningsKvittering {

	public ReturpostKvittering(EbmsApplikasjonsKvittering applikasjonsKvittering) {
		super(applikasjonsKvittering);
	}

}
