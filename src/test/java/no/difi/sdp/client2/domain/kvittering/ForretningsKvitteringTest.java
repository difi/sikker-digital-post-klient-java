package no.difi.sdp.client2.domain.kvittering;

import no.digipost.api.representations.KanBekreftesSomBehandletKvittering;
import no.digipost.api.representations.KvitteringsReferanse;
import org.junit.Test;

import java.time.Instant;

import static org.fest.assertions.api.Assertions.assertThat;

public class ForretningsKvitteringTest {

    @Test
    public void testConstructor_initializesProperly() {
        KanBekreftesSomBehandletKvittering kanBekreftesSomBehandletKvittering = new KanBekreftesSomBehandletKvittering() {
            @Override
            public String getMeldingsId() {
                return "MeldingsId";
            }

            @Override
            public KvitteringsReferanse getReferanseTilMeldingSomKvitteres() {
                return null;
            }
        };
        KvitteringsInfo2 kvitteringsInfo2 = new KvitteringsInfo2("konversasjonsId", "referanseTilMeldingId", Instant.now());
        LeveringsKvittering leveringsKvittering = new LeveringsKvittering(kanBekreftesSomBehandletKvittering, kvitteringsInfo2);

        assertThat(leveringsKvittering.kanBekreftesSomBehandletKvittering).isEqualTo(kanBekreftesSomBehandletKvittering);
        assertThat(leveringsKvittering.kvitteringsInfo2).isEqualTo(kvitteringsInfo2);
    }
}