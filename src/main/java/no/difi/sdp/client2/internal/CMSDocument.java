package no.difi.sdp.client2.internal;

public class CMSDocument {
    private final byte[] bytes;

    public CMSDocument(byte[] bytes) {
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }
}
