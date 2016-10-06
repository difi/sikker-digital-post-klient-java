package no.difi.sdp.client2.internal;

import no.digipost.api.representations.EbmsForsendelse;

public class EbmsForsendelseContainer {
    private final EbmsForsendelse ebmsForsendelse;
    private final DokumentpakkeContainer dokumentpakkeContainer;

    public EbmsForsendelseContainer(EbmsForsendelse ebmsForsendelse, DokumentpakkeContainer dokumentpakkeContainer) {
        this.ebmsForsendelse = ebmsForsendelse;
        this.dokumentpakkeContainer = dokumentpakkeContainer;
    }

    public EbmsForsendelse getEbmsForsendelse() {
        return ebmsForsendelse;
    }

    public DokumentpakkeContainer getDokumentpakkeContainer() {
        return dokumentpakkeContainer;
    }
}
