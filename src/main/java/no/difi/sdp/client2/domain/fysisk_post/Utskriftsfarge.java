package no.difi.sdp.client2.domain.fysisk_post;

import no.difi.begrep.sdp.schema_v10.SDPUtskriftsfarge;

public enum Utskriftsfarge {
	SORT_HVIT   (SDPUtskriftsfarge.SORT_HVIT),
	FARGE       (SDPUtskriftsfarge.FARGE);

	public final SDPUtskriftsfarge sdpUtskriftsfarge;

	private Utskriftsfarge(SDPUtskriftsfarge sdpUtskriftsfarge) {
		this.sdpUtskriftsfarge = sdpUtskriftsfarge;
	}

}
