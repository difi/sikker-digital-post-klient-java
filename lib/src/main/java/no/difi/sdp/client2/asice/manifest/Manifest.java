package no.difi.sdp.client2.asice.manifest;

import no.difi.sdp.client2.asice.AsicEAttachable;

public class Manifest implements AsicEAttachable {

    private final byte[] xmlBytes;

    public Manifest(byte[] xmlBytes) {
        this.xmlBytes = xmlBytes;
    }

    @Override
    public String getFileName() {
        return "manifest.xml";
    }

    @Override
    public byte[] getBytes() {
        return xmlBytes;
    }

    @Override
    public String getMimeType() {
        return "application/xml";
    }
}
