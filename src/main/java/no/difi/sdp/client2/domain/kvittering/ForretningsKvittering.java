package no.difi.sdp.client2.domain.kvittering;

import no.digipost.api.representations.KanBekreftesSomBehandletKvittering;
import no.digipost.api.representations.KvitteringsReferanse;

public abstract class ForretningsKvittering implements KanBekreftesSomBehandletKvittering{

    public final Kvitteringsinfo kvitteringsinfo;
    public final KanBekreftesSomBehandletKvittering kanBekreftesSomBehandletKvittering;

    public ForretningsKvittering(KanBekreftesSomBehandletKvittering kanBekreftesSomBehandletKvittering,  Kvitteringsinfo kvitteringsinfo){
        this.kanBekreftesSomBehandletKvittering = kanBekreftesSomBehandletKvittering;
        this.kvitteringsinfo = kvitteringsinfo;
    }

    /**
     * Gir hvilken subtype av ForretningsKvittering og konversasjonsId som String.
     * Subklasser kan override dette.
     */
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "konversasjonsId=" + kvitteringsinfo.getKonversasjonsId() +
                "}";
    }

    public String getMeldingsId(){
       return kanBekreftesSomBehandletKvittering.getMeldingsId();
    }

    public KvitteringsReferanse getReferanseTilMeldingSomKvitteres(){
        return kanBekreftesSomBehandletKvittering.getReferanseTilMeldingSomKvitteres();
    }
}
