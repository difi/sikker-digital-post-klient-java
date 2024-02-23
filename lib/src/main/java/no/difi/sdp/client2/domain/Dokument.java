/*
 * Copyright (C) Posten Norge AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package no.difi.sdp.client2.domain;

import no.difi.sdp.client2.asice.AsicEAttachable;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class Dokument implements AsicEAttachable {

    private String tittel;
    private String filnavn;
    private byte[] dokument;
    private String mimeType = "application/pdf";
    private Optional<MetadataDokument> metadataDocument;

    private Dokument(String tittel, String filnavn, byte[] dokument, MetadataDokument metadataDocument) {
        this.tittel = tittel;
        this.filnavn = filnavn;
        this.dokument = dokument;
        this.metadataDocument = Optional.ofNullable(metadataDocument);
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
    
    public Optional<MetadataDokument> getMetadataDocument() {
        return metadataDocument;
    }

    /**
     * @param tittel Tittel som vises til brukeren gitt riktig sikkerhetsnivå. Kan være null for hoveddokument
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
     * @param tittel Tittel som vises til brukeren gitt riktig sikkerhetsnivå. Kan være null for hoveddokument
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
            target = new Dokument(tittel, filnavn, dokument, null);
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
        
        public Builder metadataDocument(MetadataDokument metadataDokument){
            target.metadataDocument = Optional.of(metadataDokument);
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
