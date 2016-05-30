package no.difi.sdp.client2.domain.kvittering;

import no.digipost.api.representations.EbmsApplikasjonsKvittering;

public class LeveringsKvittering extends ForretningsKvittering {

    public LeveringsKvittering(EbmsApplikasjonsKvittering applikasjonsKvittering) {
        super(applikasjonsKvittering);
    }

}
