package no.difi.sdp.client.util;

import no.difi.sdp.client.domain.exceptions.RuntimeIOException;

import java.io.IOException;
import java.io.InputStream;

public class IOUtils {

    public static byte[] toByteArrayCloseStream(InputStream asicStream) {
        byte[] bytes;
        try {
            bytes = org.apache.commons.io.IOUtils.toByteArray(asicStream);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
        return bytes;
    }

}
