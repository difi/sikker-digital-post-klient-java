package no.difi.sdp.client;

import no.difi.sdp.client.domain.Forsendelse;
import no.difi.sdp.client.domain.TransportKvittering;

public class SikkerDigitalPostKlient {

    public TransportKvittering send(Forsendelse forsendelse) {
        return new TransportKvittering();

    }
}
