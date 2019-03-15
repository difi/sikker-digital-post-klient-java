package no.difi.sdp.client2.domain.kvittering;

import no.digipost.api.representations.KanBekreftesSomBehandletKvittering;
import no.digipost.api.representations.KvitteringsReferanse;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class ForretningsKvitteringTest {

    @Test
    public void constructor_initializes_properly() {
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
        KvitteringsInfo kvitteringsInfo = new KvitteringsInfo("konversasjonsId", "referanseTilMeldingId", Instant.now());
        LeveringsKvittering leveringsKvittering = new LeveringsKvittering(kanBekreftesSomBehandletKvittering, kvitteringsInfo);

        assertThat(leveringsKvittering.getReferanseTilMeldingSomKvitteres(), equalTo(kanBekreftesSomBehandletKvittering.getReferanseTilMeldingSomKvitteres()));
        assertThat(leveringsKvittering.getMeldingsId(), equalTo(kanBekreftesSomBehandletKvittering.getMeldingsId()));
        assertThat(leveringsKvittering.getKonversasjonsId(), equalTo(kvitteringsInfo.getKonversasjonsId()));
        assertThat(leveringsKvittering.getReferanseTilMeldingId(), equalTo(kvitteringsInfo.getReferanseTilMeldingId()));
        assertThat(leveringsKvittering.getTidspunkt(), equalTo(kvitteringsInfo.getTidspunkt()));
    }
}