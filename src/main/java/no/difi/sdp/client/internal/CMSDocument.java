package no.difi.sdp.client.internal;

public class CMSDocument {
    private final byte[] bytes;

    public CMSDocument(byte[] bytes) {
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }
}
