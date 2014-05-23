package no.difi.sdp.client.asice;

import no.difi.sdp.client.asice.archive.Archive;
import no.difi.sdp.client.asice.archive.CreateZip;
import no.difi.sdp.client.asice.manifest.CreateManifest;
import no.difi.sdp.client.asice.manifest.Manifest;
import no.difi.sdp.client.asice.signature.CreateSignature;
import no.difi.sdp.client.asice.signature.Signature;
import no.difi.sdp.client.domain.Avsender;
import no.difi.sdp.client.domain.Forsendelse;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

    public InputStream createStream(Avsender avsender, Forsendelse forsendelse) {
        // Lag Asic-E manifest
        Manifest manifest = createManifest.createManifest(avsender, forsendelse);

        List<AsicEAttachable> files = new ArrayList<AsicEAttachable>();
        files.add(forsendelse.getDokumentpakke().getHoveddokument());
        files.addAll(forsendelse.getDokumentpakke().getVedlegg());
        files.add(manifest);

        // Lag signatur over alle filene i pakka
        Signature signature = createSignature.createSignature(avsender.getNoekkelpar(), files);
        files.add(signature);

        // Zip filene
        Archive archive = createZip.zipIt(files);

        if (debug_writeToDisk != null) {
            writeArchiveToDisk(archive);
        }

        return new ByteArrayInputStream(archive.getBytes());
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

        FileOutputStream output = null;
        try {
            output = new FileOutputStream(file);
            IOUtils.copy(new ByteArrayInputStream(archive.getBytes()), output);
            log.info("Wrote Asic-E debug file to " + file.getAbsolutePath());
        } catch (IOException e) {
            log.error("Could not write Asic-E file", e);
        } finally {
            IOUtils.closeQuietly(output);
        }
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
