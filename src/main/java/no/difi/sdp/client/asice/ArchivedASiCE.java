package no.difi.sdp.client.asice;

public class ArchivedASiCE {

    private final byte[] bytes;

    public ArchivedASiCE(byte[] bytes) {
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }
}
