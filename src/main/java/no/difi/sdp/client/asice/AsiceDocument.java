package no.difi.sdp.client.asice;

public class AsiceDocument {

    private final byte[] bytes;

    public AsiceDocument(byte[] bytes) {
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }
}
