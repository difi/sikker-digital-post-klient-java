package no.difi.sdp.client2;

public class SendResultat {

    private final String meldingsId;
    private final String referanseTilMeldingsId;
    private final long fakturerbareBytes;

    public SendResultat(String meldingsId, String referanseTilMeldingsId, long fakturerbareBytes) {
        this.meldingsId = meldingsId;
        this.referanseTilMeldingsId = referanseTilMeldingsId;
        this.fakturerbareBytes = fakturerbareBytes;
    }

    public String getMeldingsId() {
        return meldingsId;
    }

    public String getReferanseTilMeldingsId() {
        return referanseTilMeldingsId;
    }

    public long getFakturerbareBytes() {
        return fakturerbareBytes;
    }
}
