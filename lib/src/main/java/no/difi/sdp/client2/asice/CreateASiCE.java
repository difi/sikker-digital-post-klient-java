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
package no.difi.sdp.client2.asice;

import no.difi.sdp.client2.asice.archive.Archive;
import no.difi.sdp.client2.asice.archive.CreateZip;
import no.difi.sdp.client2.asice.manifest.CreateManifest;
import no.difi.sdp.client2.asice.manifest.Manifest;
import no.difi.sdp.client2.asice.signature.CreateSignature;
import no.difi.sdp.client2.asice.signature.Signature;
import no.difi.sdp.client2.domain.Databehandler;
import no.difi.sdp.client2.domain.Forsendelse;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateASiCE {

    private final CreateManifest createManifest;
    private final CreateSignature createSignature;
    private final CreateZip createZip;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static File debug_writeToDisk = null;

    public CreateASiCE() {
        createManifest = new CreateManifest();
        createSignature = new CreateSignature();
        createZip = new CreateZip();
    }

    public ArchivedASiCE createAsice(Databehandler databehandler, Forsendelse forsendelse) {
        // Lag ASiC-E manifest
        log.info("Creating ASiC-E manifest");
        Manifest manifest = createManifest.createManifest(forsendelse);

        List<AsicEAttachable> files = new ArrayList<AsicEAttachable>();
        files.add(forsendelse.getDokumentpakke().getHoveddokument());
        files.addAll(forsendelse.getDokumentpakke().getVedlegg());
        forsendelse.getDokumentpakke().getHoveddokument().getMetadataDocument().ifPresent(files::add);
        files.add(manifest);

        // Lag signatur over alle filene i pakka
        log.info("Signing ASiC-E documents using private key with alias " + databehandler.noekkelpar.getAlias());
        Signature signature = createSignature.createSignature(databehandler.noekkelpar, files);
        files.add(signature);

        // Zip filene
        log.trace("Zipping ASiC-E files. Contains a total of " + files.size() + " files (including the generated manifest and signatures)");
        Archive archive = createZip.zipIt(files);

        if (debug_writeToDisk != null) {
            writeArchiveToDisk(archive);
        }

        return new ArchivedASiCE(archive.getBytes(), getUnzippedContentBytesCount(files));
    }

    private void writeArchiveToDisk(Archive archive) {
        log.error("Writing Asic-E to disk for debug");
        File file;
        if (debug_writeToDisk.isDirectory()) {
            file = new File(debug_writeToDisk, "asic-" + System.currentTimeMillis() + ".zip");
        }
        else {
            file = debug_writeToDisk;
        }

        try (FileOutputStream output = new FileOutputStream(file)) {
            IOUtils.write(archive.getBytes(), output);
            log.info("Wrote Asic-E debug file to " + file.getAbsolutePath());
        } catch (IOException e) {
            log.error("Could not write Asic-E file", e);
        }
    }


    static long getUnzippedContentBytesCount(List<AsicEAttachable> attachables){
        return attachables.stream()
                .mapToLong(a -> a.getBytes().length)
                .sum();
    }

    /**
     * Skriv zippet Asic-E til disk for manuell inspeksjon.
     *
     * @deprecated Ikke for produksjonsbruk.
     *
     * @param location Mappe eller fil asic-filen skal skrives til. Hvis det er en mappe vil filnavn bli generert automatisk.
     */
    @Deprecated
    public static void debug_writeArchiveToDisk(File location) {
        debug_writeToDisk = location;
    }
}
