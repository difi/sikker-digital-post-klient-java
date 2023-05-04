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
package no.difi.sdp.client2.asice.archive;

import no.difi.sdp.client2.asice.AsicEAttachable;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

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
    @Disabled("WARN! Write zip file to to disk test is disabled.")
    public void write_file_to_disk() throws IOException {
        CreateZip createZip = new CreateZip();

        List<AsicEAttachable> asicEAttachables = asList(
                file("file.txt", "test"),
                file("file2.txt", "test2")
        );

        Archive archive = createZip.zipIt(asicEAttachables);

        Path tempFile = File.createTempFile("test", ".zip").toPath().toAbsolutePath();
        Files.write(tempFile, archive.getBytes());
        System.out.println("Skrev zip-fil til " + tempFile);
    }

    private void verifyZipFile(ZipInputStream zipInputStream, String fileName, String contents) throws IOException {
        ZipEntry firstZipFile = zipInputStream.getNextEntry();
        assertThat(firstZipFile.getName(), containsString(fileName));
        assertThat(IOUtils.toByteArray(zipInputStream), equalTo(contents.getBytes()));
    }

    private AsicEAttachable file(final String fileName, final String contents) {
        return new AsicEAttachable() {
            @Override
            public String getFileName() {
                return fileName;
            }

            @Override
            public byte[] getBytes() {
                return contents.getBytes();
            }

            @Override
            public String getMimeType() {
                return "application/txt";
            }
        };
    }

}
