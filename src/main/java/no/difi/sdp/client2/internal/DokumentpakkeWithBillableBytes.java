package no.difi.sdp.client2.internal;


import no.digipost.api.representations.Dokumentpakke;

class DokumentpakkeWithBillableBytes {

    private final Dokumentpakke dokumentpakke;
    private final long billableBytes;

    DokumentpakkeWithBillableBytes(Dokumentpakke dokumentpakke, long billableBytes){

        this.dokumentpakke = dokumentpakke;
        this.billableBytes = billableBytes;
    }

    public Dokumentpakke getDokumentpakke() {
        return dokumentpakke;
    }

    public long getBillableBytes() {
        return billableBytes;
    }
}
