package no.difi.sdp.client.asice;

public class Signature implements AsicEAttachable {

    private final byte[] xmlBytes;

    public Signature(byte[] xmlBytes) {
        this.xmlBytes = xmlBytes;
    }

    @Override
    public String getFileName() {
        return "signatures.xml";
    }

    public byte[] getBytes() {
        return xmlBytes;
    }

    @Override
    public String getMimeType() {
        return null;
    }
}
