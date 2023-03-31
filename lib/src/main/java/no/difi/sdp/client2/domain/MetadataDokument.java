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

public class MetadataDokument implements AsicEAttachable {

    private String filnavn;
    private byte[] dokument;
    private String mimeType;

    public MetadataDokument(String filnavn, String mimeType, byte[] dokument) {
        this.filnavn = filnavn;
        this.mimeType = mimeType;
        this.dokument = dokument;
    }

    /**
     * @param filnavn   Tittel som vises til brukeren gitt riktig sikkerhetsnivå.
     * @param mimetype  Filnavnet til dokumentet.
     * @param dokument Dokumentet som en strøm.
     */
    public static Builder builder(String filnavn, String mimetype, InputStream dokument) {
        try (InputStream dokumentStreamToConsume = dokument) {
            byte[] dokumentBytes = IOUtils.toByteArray(dokumentStreamToConsume);
            return new Builder(filnavn, mimetype, dokumentBytes);
        } catch (IOException e) {
            throw new Dokument.LastDokumentException("Kunne ikke lese dokument", e);
        }
    }

    /**
     * @param filnavn  Tittel som vises til brukeren gitt riktig sikkerhetsnivå.
     * @param mimetype  Filnavnet til dokumentet.
     * @param dokument Filen som skal sendes. Navnet på filen vil brukes som filnavn ovenfor mottaker.
     */
    public static Builder builder(String filnavn, String mimetype, byte[] dokument) {
        return new Builder(filnavn, mimetype, dokument);
    }

    /**
     * @param mimetype Mimetype til begrepet
     * @param file   Filen som skal sendes. Navnet på filen vil brukes som filnavn ovenfor mottaker.
     */
    public static Builder builder(String mimetype, File file) {
        try {
            return builder(file.getName(), mimetype, new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new Dokument.LastDokumentException("Fant ikke fil", e);
        }
    }

    @Override
    public String getFileName() {
        return this.filnavn;
    }

    @Override
    public byte[] getBytes() {
        return this.dokument;
    }

    @Override
    public String getMimeType() {
        return this.mimeType;
    }

    public static class Builder {

        private final MetadataDokument target;
        private boolean built = false;

        private Builder(String filnavn, String mimetype, byte[] dokument) {
            target = new MetadataDokument(filnavn, mimetype, dokument);
        }

        public MetadataDokument build() {
            if (built) {
                throw new IllegalStateException("Can't build twice");
            }
            built = true;
            return target;
        }
    }
}
