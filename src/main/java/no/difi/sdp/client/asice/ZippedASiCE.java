package no.difi.sdp.client.asice;

public class ZippedASiCE {

    private final byte[] bytes;

    public ZippedASiCE(byte[] bytes) {
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }
}
