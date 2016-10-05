package no.difi.sdp.client2.domain;

import no.difi.sdp.client2.asice.AsicEAttachable;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class Dokument implements AsicEAttachable {

    private String tittel;
    private String filnavn;
    private byte[] dokument;
    private String mimeType = "application/pdf";

    private Dokument(String tittel, String filnavn, byte[] dokument) {
        this.tittel = tittel;
        this.filnavn = filnavn;
        this.dokument = dokument;
    }

    @Override
    public String getFileName() {
        return getFilnavn();
    }

    @Override
    public byte[] getBytes() {
        return dokument;
    }

    public String getFilnavn() {
        return filnavn;
    }

    @Override
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
        try (InputStream dokumentStreamToConsume = dokument) {
            byte[] dokumentBytes = IOUtils.toByteArray(dokumentStreamToConsume);
            return new Builder(tittel, filnavn, dokumentBytes);
        } catch (IOException e) {
            throw new LastDokumentException("Kunne ikke lese dokument", e);
        }
    }

    /**
     * @param tittel Tittel som vises til brukeren gitt riktig sikkerhetsnivå.
     * @param filnavn Filnavnet til dokumentet.
     * @param dokument Filen som skal sendes. Navnet på filen vil brukes som filnavn ovenfor mottaker.
     */
    public static Builder builder(String tittel, String filnavn, byte[] dokument) {
        return new Builder(tittel, filnavn, dokument);
    }

    /**
     * @param tittel Tittel som vises til brukeren gitt riktig sikkerhetsnivå.
     * @param file Filen som skal sendes. Navnet på filen vil brukes som filnavn ovenfor mottaker.
     */
    public static Builder builder(String tittel, File file) {
        try {
            return builder(tittel, file.getName(), new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new LastDokumentException("Fant ikke fil", e);
        }
    }



    public static class Builder {

        private final Dokument target;
        private boolean built = false;

        private Builder(String tittel, String filnavn, byte[] dokument) {
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

    public static class LastDokumentException extends RuntimeException {
        public LastDokumentException(String message, Exception e) {
            super(message, e);
        }
    }
}
