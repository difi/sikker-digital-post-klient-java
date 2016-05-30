package no.difi.sdp.client2.domain.fysisk_post;

import no.difi.begrep.sdp.schema_v10.SDPFysiskPostReturhaandtering;

public enum Returhaandtering {

	DIREKTE_RETUR            (SDPFysiskPostReturhaandtering.DIREKTE_RETUR),
	MAKULERING_MED_MELDING   (SDPFysiskPostReturhaandtering.MAKULERING_MED_MELDING);


	public final SDPFysiskPostReturhaandtering sdpReturhaandtering;

	private Returhaandtering(SDPFysiskPostReturhaandtering sdpReturhaandtering) {
		this.sdpReturhaandtering = sdpReturhaandtering;
	}
}
