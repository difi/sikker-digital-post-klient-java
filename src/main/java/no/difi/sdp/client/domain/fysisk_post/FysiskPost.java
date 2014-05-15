package no.difi.sdp.client.domain.fysisk_post;

import java.security.cert.X509Certificate;

public class FysiskPost {

    private FysiskPost(String orgNummerPrintleverandoer, X509Certificate printleverandoerSertifikat, NorskPostadresse norskPostadresse, UtenlandskPostadresse utenlandskPostadresse, NorskPostadresse returadresse) {
        if ((norskPostadresse != null && utenlandskPostadresse != null) || (norskPostadresse == null && utenlandskPostadresse == null)) {
            throw new IllegalArgumentException("Must set either norsk postadresse or utenlandsk postadresse");
        }

        this.utenlandskAdresse = utenlandskPostadresse;
        this.norskAdresse = norskPostadresse;
        this.orgNummerPrintleverandoer = orgNummerPrintleverandoer;
        this.printleverandoerSertifikat = printleverandoerSertifikat;
        this.returadresse = returadresse;
    }

    private String orgNummerPrintleverandoer;
    private X509Certificate printleverandoerSertifikat;

    private UtenlandskPostadresse utenlandskAdresse;
    private NorskPostadresse norskAdresse;
    private NorskPostadresse returadresse;

    private PostType postType;


    public static Builder builder(String orgNummerPrintleverandoer, X509Certificate printleverandoerSertifikat, NorskPostadresse norskAdresse, NorskPostadresse returadresse) {
        return new Builder(orgNummerPrintleverandoer, printleverandoerSertifikat, norskAdresse, null, returadresse);
    }

    public static Builder builder(String orgNummerPrintleverandoer, X509Certificate printleverandoerSertifikat, UtenlandskPostadresse utenlandskAdresse, NorskPostadresse returadresse) {
        return new Builder(orgNummerPrintleverandoer, printleverandoerSertifikat, null, utenlandskAdresse, returadresse);
    }

    public static class Builder {

        private final FysiskPost target;
        private boolean built = false;

        private Builder(String orgNummerPrintleverandoer, X509Certificate printleverandoerSertifikat, NorskPostadresse norskAdresse, UtenlandskPostadresse utenlandskAdresse, NorskPostadresse returadresse) {
            target = new FysiskPost(orgNummerPrintleverandoer, printleverandoerSertifikat, norskAdresse, utenlandskAdresse, returadresse);
        }

        public Builder postType(PostType postType) {
            target.postType = postType;
            return this;
        }

        public FysiskPost build() {
            if (built) throw new IllegalStateException("Can't build twice");
            built = true;

            return target;
        }
    }
}
