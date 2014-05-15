package no.difi.sdp.client.domain.fysisk_post;

public class NorskPostadresse {

    private NorskPostadresse(String navn, String adresselinje1, String adresselinje2, String postnummer, String poststed) {
        this.navn = navn;
        this.adresselinje1 = adresselinje1;
        this.adresselinje2 = adresselinje2;
        this.postnummer = postnummer;
        this.poststed = poststed;
    }

    private String navn;
    private String adresselinje1;
    private String adresselinje2;
    private String postnummer;
    private String poststed;

    public static Builder builder(String navn, String adresselinje1, String adresselinje2, String postnummer, String poststed) {
        return new Builder(navn, adresselinje1, adresselinje2, postnummer, poststed);
    }

    public static class Builder {

        private final NorskPostadresse target;
        private boolean built = false;

        private Builder(String navn, String adresselinje1, String adresselinje2, String postnummer, String poststed) {
            this.target = new NorskPostadresse(navn, adresselinje1, adresselinje2, postnummer, poststed);
        }

        public NorskPostadresse build() {
            if (built) throw new IllegalStateException("Can't build twice");
            built = true;

            return target;
        }
    }
}
