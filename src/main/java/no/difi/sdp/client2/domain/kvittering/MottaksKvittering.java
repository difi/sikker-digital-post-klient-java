package no.difi.sdp.client2.domain.kvittering;

import no.digipost.api.representations.EbmsApplikasjonsKvittering;

public class MottaksKvittering extends ForretningsKvittering {

	public MottaksKvittering(EbmsApplikasjonsKvittering applikasjonsKvittering) {
		super(applikasjonsKvittering);
	}

}
