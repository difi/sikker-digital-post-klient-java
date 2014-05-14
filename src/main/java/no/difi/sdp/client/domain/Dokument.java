package no.difi.sdp.client.domain;

import java.io.InputStream;

public class Dokument {

    private Dokument(String tittel, String filnavn, InputStream dokument) {
        this.tittel = tittel;
        this.filnavn = filnavn;
        this.dokument = dokument;
    }

    /**
     * Tittel som vises til brukeren gitt riktig sikkerhetsnivå.
     */
    private String tittel;

    /**
     * MIME-type for dokumentet. For informasjon om tillatte formater, se <a href="http://begrep.difi.no/SikkerDigitalPost/Dokumentformat/">http://begrep.difi.no/SikkerDigitalPost/Dokumentformat/</a> }.
     *
     * Standard er application/pdf.
     */
    private String mimeType = "application/pdf";

    /**
     * Filnavnet til dokumentet.
     */
    private String filnavn;

    /**
     * Dokumentet som en strøm.
     */
    private InputStream dokument;

    public static Builder builder(String tittel, String filnavn, InputStream dokument) {
        return new Builder(tittel, filnavn, dokument);
    }

    public static class Builder {

        private final Dokument target;

        public Builder(String tittel, String filnavn, InputStream dokument) {
            target = new Dokument(tittel, filnavn, dokument);
        }

        public Builder mimeType(String mimeType) {
            target.mimeType = mimeType;
            return this;
        }

        public Dokument build() {
            return target;
        }
    }
}
