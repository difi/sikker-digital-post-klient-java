package no.difi.sdp.client;

import no.difi.sdp.client.domain.Avsender;
import no.difi.sdp.client.domain.Forsendelse;
import no.difi.sdp.client.domain.TransportKvittering;

public class SikkerDigitalPostKlient {

    public SikkerDigitalPostKlient(Avsender avsender, KlientKonfigurasjon konfigurasjon) {
    }

    public TransportKvittering send(Forsendelse forsendelse) {
        return new TransportKvittering();
    }
}
