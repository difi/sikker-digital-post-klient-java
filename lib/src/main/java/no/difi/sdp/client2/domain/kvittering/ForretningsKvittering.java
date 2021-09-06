package no.difi.sdp.client2.domain.kvittering;

import no.digipost.api.representations.KanBekreftesSomBehandletKvittering;
import no.digipost.api.representations.KvitteringsReferanse;

import java.time.Instant;

public abstract class ForretningsKvittering implements KanBekreftesSomBehandletKvittering {

    private final KvitteringsInfo kvitteringsInfo;
    private final KanBekreftesSomBehandletKvittering kanBekreftesSomBehandletKvittering;

    public ForretningsKvittering(KanBekreftesSomBehandletKvittering kanBekreftesSomBehandletKvittering,  KvitteringsInfo kvitteringsInfo){
        this.kanBekreftesSomBehandletKvittering = kanBekreftesSomBehandletKvittering;
        this.kvitteringsInfo = kvitteringsInfo;
    }

    /**
     * Gir hvilken subtype av ForretningsKvittering og konversasjonsId som String.
     * Subklasser kan override dette.
     */
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "konversasjonsId=" + kvitteringsInfo.getKonversasjonsId() +
                "}";
    }

    public Instant getTidspunkt() {
        return kvitteringsInfo.getTidspunkt();
    }

    public String getKonversasjonsId() {
        return kvitteringsInfo.getKonversasjonsId();
    }

    public String getReferanseTilMeldingId() {
        return kvitteringsInfo.getReferanseTilMeldingId();
    }

    @Override
    public String getMeldingsId(){
       return kanBekreftesSomBehandletKvittering.getMeldingsId();
    }

    @Override
    public KvitteringsReferanse getReferanseTilMeldingSomKvitteres(){
        return kanBekreftesSomBehandletKvittering.getReferanseTilMeldingSomKvitteres();
    }

}
