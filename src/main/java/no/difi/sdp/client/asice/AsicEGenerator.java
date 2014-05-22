package no.difi.sdp.client.asice;

import no.difi.sdp.client.asice.signature.CreateSignature;
import no.difi.sdp.client.domain.Avsender;
import no.difi.sdp.client.domain.Forsendelse;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AsicEGenerator {

    private final CreateManifest createManifest;
    private final CreateSignature createSignature;
    private final CreateZip createZip;

    public AsicEGenerator() {
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

        return new ByteArrayInputStream(archive.getBytes());
    }

}
