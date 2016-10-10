package no.difi.sdp.client2.internal;

import no.digipost.api.representations.EbmsForsendelse;

public class ForsendelseBundleWithBillableBytes {
    private final EbmsForsendelse ebmsForsendelse;
    private final long dokumentpakkeContainer;

    public ForsendelseBundleWithBillableBytes(EbmsForsendelse ebmsForsendelse, long billableBytes) {
        this.ebmsForsendelse = ebmsForsendelse;
        this.dokumentpakkeContainer = billableBytes;
    }

    public EbmsForsendelse getEbmsForsendelse() {
        return ebmsForsendelse;
    }

    public long getBillableBytes() {
        return dokumentpakkeContainer;
    }
}
