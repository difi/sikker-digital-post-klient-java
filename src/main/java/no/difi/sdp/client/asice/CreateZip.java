package no.difi.sdp.client.asice;

import no.difi.sdp.client.domain.exceptions.RuntimeIOException;
import org.apache.commons.codec.Charsets;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CreateZip {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    Archive zipIt(List<AsicEAttachable> files) {
        ByteArrayOutputStream archive = null;
        ZipOutputStream zipOutputStream = null;
        try {
            log.trace("Zipping " + files.size() + " files");
            archive = new ByteArrayOutputStream();
            zipOutputStream = new ZipOutputStream(archive, Charsets.UTF_8);
            zipOutputStream.setMethod(ZipOutputStream.DEFLATED);
            for (AsicEAttachable file : files) {
                log.trace("Adding " + file.getFileName() + " to archive. Size in bytes before compression: " + file.getBytes().length);
                ZipEntry zipEntry = new ZipEntry(file.getFileName());
                zipEntry.setSize(file.getBytes().length);

                zipOutputStream.putNextEntry(zipEntry);
                IOUtils.copy(new ByteArrayInputStream(file.getBytes()), zipOutputStream);
                zipOutputStream.closeEntry();
            }
            zipOutputStream.finish();
            zipOutputStream.close();

            return new Archive(archive.toByteArray());
        }
        catch (IOException e) {
            throw new RuntimeIOException(e);
        }
        finally {
            IOUtils.closeQuietly(archive);
            IOUtils.closeQuietly(zipOutputStream);
        }
    }
}
