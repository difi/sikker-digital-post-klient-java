package no.difi.sdp.client2.asice;

public class ArchivedASiCE {

    private final byte[] bytes;
    private long unzippedContentBytesCount;

    ArchivedASiCE(byte[] bytes, long unzippedContentBytesCount) {
        this.bytes = bytes;
        this.unzippedContentBytesCount = unzippedContentBytesCount;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public long getUnzippedContentBytesCount() { return unzippedContentBytesCount; }
}
