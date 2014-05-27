package no.difi.sdp.client.asice.archive;

import no.difi.sdp.client.asice.AsicEAttachable;
import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static java.util.Arrays.asList;
import static org.fest.assertions.api.Assertions.assertThat;

public class CreateZipTest {

    @Test
    public void test_create_zip_file_readable_by_java() throws IOException {
        CreateZip createZip = new CreateZip();

        List<AsicEAttachable> asicEAttachables = asList(
                file("file.txt", "test"),
                file("file2.txt", "test2")
        );

        Archive archive = createZip.zipIt(asicEAttachables);

        ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(archive.getBytes()));

        verifyZipFile(zipInputStream, "file.txt", "test");
        verifyZipFile(zipInputStream, "file2.txt", "test2");
    }

    @Test
    @Ignore("Writes files to disk. Can be useful for debugging")
    public void write_file_to_disk() throws IOException {
        CreateZip createZip = new CreateZip();

        List<AsicEAttachable> asicEAttachables = asList(
                file("file.txt", "test"),
                file("file2.txt", "test2")
        );

        Archive archive = createZip.zipIt(asicEAttachables);

        File tempFile = File.createTempFile("test", ".zip");
        IOUtils.copy(new ByteArrayInputStream(archive.getBytes()), new FileOutputStream(tempFile));
        System.out.println("Skrev zip-fil til " + tempFile.getAbsolutePath());
    }

    private void verifyZipFile(ZipInputStream zipInputStream, String fileName, String contents) throws IOException {
        ZipEntry firstZipFile = zipInputStream.getNextEntry();
        assertThat(firstZipFile.getName()).contains(fileName);
        assertThat(IOUtils.toByteArray(zipInputStream)).isEqualTo(contents.getBytes());
    }

    private AsicEAttachable file(final String fileName, final String contents) {
        return new AsicEAttachable() {
            public String getFileName() { return fileName; }
            public byte[] getBytes() { return contents.getBytes(); }
            public String getMimeType() { return "application/txt"; }
        };
    }

}
