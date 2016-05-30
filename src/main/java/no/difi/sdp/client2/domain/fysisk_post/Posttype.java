package no.difi.sdp.client2.domain.fysisk_post;

import no.difi.begrep.sdp.schema_v10.SDPFysiskPostType;

public enum Posttype {
	A_PRIORITERT (SDPFysiskPostType.A),
	B_OEKONOMI   (SDPFysiskPostType.B);

	public final SDPFysiskPostType sdpType;

	Posttype(SDPFysiskPostType sdpType) {
		this.sdpType = sdpType;
	}
}
