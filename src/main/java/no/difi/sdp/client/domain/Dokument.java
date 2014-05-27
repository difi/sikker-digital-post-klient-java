package no.difi.sdp.client.domain;

import java.io.InputStream;

public class Dokument {

    private Dokument(String tittel, String filnavn, InputStream dokument) {
        this.tittel = tittel;
        this.filnavn = filnavn;
        this.dokument = dokument;
    }

    private String tittel;
    private String filnavn;
    private InputStream dokument;
    private String mimeType = "application/pdf";

    public String getFilnavn() {
        return filnavn;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getTittel() {
        return tittel;
    }

    /**
     * @param tittel Tittel som vises til brukeren gitt riktig sikkerhetsnivå.
     * @param filnavn Filnavnet til dokumentet.
     * @param dokument Dokumentet som en strøm.
     */
    public static Builder builder(String tittel, String filnavn, InputStream dokument) {
        return new Builder(tittel, filnavn, dokument);
    }

    public static class Builder {

        private final Dokument target;
        private boolean built = false;

        private Builder(String tittel, String filnavn, InputStream dokument) {
            target = new Dokument(tittel, filnavn, dokument);
        }

        /**
         * MIME-type for dokumentet. For informasjon om tillatte formater, se <a href="http://begrep.difi.no/SikkerDigitalPost/Dokumentformat/">http://begrep.difi.no/SikkerDigitalPost/Dokumentformat/</a> }.
         *
         * Standard er application/pdf.
         */
        public Builder mimeType(String mimeType) {
            target.mimeType = mimeType;
            return this;
        }

        public Dokument build() {
            if (built) throw new IllegalStateException("Can't build twice");
            built = true;
            return target;
        }
    }
}
