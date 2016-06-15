package no.difi.sdp.client2.domain.kvittering;

import no.digipost.api.representations.KanBekreftesSomBehandletKvittering;
import no.digipost.api.representations.KvitteringsReferanse;

public abstract class ForretningsKvittering implements KanBekreftesSomBehandletKvittering{

    public final KvitteringsInfo kvitteringsInfo;
    public final KanBekreftesSomBehandletKvittering kanBekreftesSomBehandletKvittering;

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

    public String getMeldingsId(){
       return kanBekreftesSomBehandletKvittering.getMeldingsId();
    }

    public KvitteringsReferanse getReferanseTilMeldingSomKvitteres(){
        return kanBekreftesSomBehandletKvittering.getReferanseTilMeldingSomKvitteres();
    }
}
