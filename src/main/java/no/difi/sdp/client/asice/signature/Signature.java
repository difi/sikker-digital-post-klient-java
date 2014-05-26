package no.difi.sdp.client.asice.signature;

import no.difi.sdp.client.asice.AsicEAttachable;

public class Signature implements AsicEAttachable {

    private final byte[] xmlBytes;

    public Signature(byte[] xmlBytes) {
        this.xmlBytes = xmlBytes;
    }

    @Override
    public String getFileName() {
        return "META-INF/signatures.xml";
    }

    public byte[] getBytes() {
        return xmlBytes;
    }

    @Override
    public String getMimeType() {
        return "application/xml";
    }
}
