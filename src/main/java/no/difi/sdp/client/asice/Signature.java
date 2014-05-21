package no.difi.sdp.client.asice;

public class Signature {

    private final byte[] xmlBytes;

    public Signature(byte[] xmlBytes) {
        this.xmlBytes = xmlBytes;
    }

    public byte[] getBytes() {
        return xmlBytes;
    }
}
