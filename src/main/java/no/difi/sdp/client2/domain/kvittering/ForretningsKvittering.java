package no.difi.sdp.client2.domain.kvittering;

public abstract class ForretningsKvittering {

    public final Kvitteringsinfo kvitteringsinfo;
    public final KvitteringBekreftbar kvitteringBekreftbar;


    public ForretningsKvittering(KvitteringBekreftbar kvitteringBekreftbar, Kvitteringsinfo kvitteringsinfo){
        this.kvitteringBekreftbar = kvitteringBekreftbar;
        this.kvitteringsinfo = kvitteringsinfo;
    }

    /**
     * Gir hvilken subtype av ForretningsKvittering og konversasjonsId som String.
     * Subklasser kan override dette.
     */
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "konversasjonsId=" + kvitteringsinfo.konversasjonsId +
                "}";
    }

    //    protected ForretningsKvittering(EbmsApplikasjonsKvittering ebmsApplikasjonsKvittering) {
//        this.applikasjonsKvittering = ebmsApplikasjonsKvittering;
////        SimpleStandardBusinessDocument sbd = ebmsApplikasjonsKvittering.getStandardBusinessDocument();
////        if (sbd.erFeil()) {
////            this.tidspunkt = sbd.getFeil().getTidspunkt();
////        } else if (sbd.erKvittering()) {
////            this.tidspunkt = sbd.getKvittering().kvittering.getTidspunkt();
////        } else {
////            throw new IllegalStateException("Unable to handle StandardBusinessDocument of type " +
////                    sbd.getUnderlyingDoc().getClass() + ", conversationId=" + sbd.getConversationId());
////        }
//
//    }

//    public String getKonversasjonsId() {
////        return applikasjonsKvittering.getStandardBusinessDocument().getConversationId();
//        return konversasjonsId;
//    }

//    public String getReferanser(){
//        return ebmsBekreftbar.getReferanser();
//        List<Reference> reference = applikasjonsKvittering.references;
//        StringResult stringResult = new StringResult();
//        Jaxb2Marshaller marshallerSingleton = Marshalling.getMarshallerSingleton();
//
//
//        Marshalling.marshal(marshallerSingleton, reference, stringResult );
//
//        Object unmarshal = marshallerSingleton.unmarshal(new StreamSource(new StringReader(stringResult.toString())));


//        String marshalled = marshallerSingleton.marshal();
//        return "";
//    }

}
