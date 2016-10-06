package no.difi.sdp.client2.internal;


import no.digipost.api.representations.Dokumentpakke;

public class DokumentpakkeContainer {

    private final Dokumentpakke dokumentpakke;
    private final long antallFakturerbareBytes;

    DokumentpakkeContainer(Dokumentpakke dokumentpakke, long antallFakturerbareBytes){

        this.dokumentpakke = dokumentpakke;
        this.antallFakturerbareBytes = antallFakturerbareBytes;
    }

    public Dokumentpakke getDokumentpakke() {
        return dokumentpakke;
    }

    public long getAntallFakturerbareBytes() {
        return antallFakturerbareBytes;
    }
}
