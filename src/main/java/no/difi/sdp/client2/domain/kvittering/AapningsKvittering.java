package no.difi.sdp.client2.domain.kvittering;

import no.digipost.api.representations.EbmsApplikasjonsKvittering;

public class AapningsKvittering extends ForretningsKvittering {

    public AapningsKvittering(EbmsApplikasjonsKvittering applikasjonsKvittering) {
        super(applikasjonsKvittering);
    }

}
