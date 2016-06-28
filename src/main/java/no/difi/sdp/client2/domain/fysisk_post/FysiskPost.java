package no.difi.sdp.client2.domain.fysisk_post;

import no.difi.sdp.client2.domain.TekniskMottaker;

public class FysiskPost {

    private KonvoluttAdresse adressat;
    private Posttype posttype;
    private Utskriftsfarge utskriftsfarge;
    private Returhaandtering returhaandtering;
    private KonvoluttAdresse returadresse;
    private TekniskMottaker utskriftsleverandoer;

    public KonvoluttAdresse getAdresse() {
        return adressat;
    }

    public Posttype getPosttype() {
        return posttype;
    }

    public Utskriftsfarge getUtskriftsfarge() {
        return utskriftsfarge;
    }

    public Returhaandtering getReturhaandtering() {
        return returhaandtering;
    }

    public KonvoluttAdresse getReturadresse() {
        return returadresse;
    }

    public TekniskMottaker getUtskriftsleverandoer() {
        return utskriftsleverandoer;
    }

    public static FysiskPost.Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private final FysiskPost fysiskPost;
        private boolean built = false;

        private Builder() {
            fysiskPost = new FysiskPost();
        }

        public Builder adresse(KonvoluttAdresse adresse) {
            fysiskPost.adressat = adresse;
            return this;
        }

        public Builder sendesMed(Posttype posttype) {
            fysiskPost.posttype = posttype;
            return this;
        }

        public Builder utskrift(Utskriftsfarge utskriftsfarge, TekniskMottaker utskriftsleverandoer) {
            fysiskPost.utskriftsfarge = utskriftsfarge;
            fysiskPost.utskriftsleverandoer = utskriftsleverandoer;
            return this;
        }

        public Builder retur(Returhaandtering haandtering, KonvoluttAdresse returadresse) {
            fysiskPost.returhaandtering = haandtering;
            fysiskPost.returadresse = returadresse;
            return this;
        }

        public FysiskPost build() {
            if (built) throw new IllegalStateException("Can't build twice");
            built = true;
            return fysiskPost;
        }

    }
}
