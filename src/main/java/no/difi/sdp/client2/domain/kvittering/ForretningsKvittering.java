package no.difi.sdp.client2.domain.kvittering;

import no.digipost.api.representations.KanBekreftesSomBehandletKvittering;

public abstract class ForretningsKvittering {

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
    //        Marshalling.marshal(marshallerSingleton, reference, stringResult );
//
//        Object unmarshal = marshallerSingleton.unmarshal(new StreamSource(new StringReader(stringResult.toString())));


//        String marshalled = marshallerSingleton.marshal();
//        return "";
//    }

}
