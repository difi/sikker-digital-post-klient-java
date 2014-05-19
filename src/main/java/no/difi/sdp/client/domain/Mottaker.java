package no.difi.sdp.client.domain;

public class Mottaker {

    private Mottaker(String personidentifikator, String postkasseadresse, Sertifikat mottakerSertifikat, String organisasjonsnummerPostkasse) {
        this.personidentifikator = personidentifikator;
        this.postkasseadresse = postkasseadresse;
        this.mottakerSertifikat = mottakerSertifikat;
        this.organisasjonsnummerPostkasse = organisasjonsnummerPostkasse;
    }

    private String personidentifikator;
    private String postkasseadresse;
    private Sertifikat mottakerSertifikat;
    private String organisasjonsnummerPostkasse;

    public String getOrganisasjonsnummerPostkasse() {
        return organisasjonsnummerPostkasse;
    }

    public String getPostkasseadresse() {
        return postkasseadresse;
    }

    public String getPersonidentifikator() {
        return personidentifikator;
    }

    public Sertifikat getSertifikat() {
        return mottakerSertifikat;
    }

    /**
     * Informasjon om mottaker. Vil vanligvis være hentet fra <a href="http://begrep.difi.no/Oppslagstjenesten/">Oppslagstjenesten</a>.
     *
     * @param personidentifikator Identifikator (fødselsnummer eller D-nummer) til mottaker av brevet.
     * @param postkasseadresse Mottakerens adresse hos postkasseleverandøren.
     * @param mottakerSertifikat Mottakers sertifikat.
     * @param organisasjonsnummerPostkasse Identifikator (organisasjonsnummer) til virksomheten som er sluttmottaker i meldingsprosessen.
     */
    public static Builder builder(String personidentifikator, String postkasseadresse, Sertifikat mottakerSertifikat, String organisasjonsnummerPostkasse) {
        return new Builder(personidentifikator, postkasseadresse, mottakerSertifikat, organisasjonsnummerPostkasse);
    }

    public static class Builder {
        private final Mottaker target;
        private boolean built = false;

        private Builder(String personidentifikator, String postkasseadresse, Sertifikat mottakerSertifikat, String organisasjonsnummerPostkasse) {
            target = new Mottaker(personidentifikator, postkasseadresse, mottakerSertifikat, organisasjonsnummerPostkasse);
        }

        public Mottaker build() {
            if (built) throw new IllegalStateException("Can't build twice");
            built = true;
            return target;
        }
    }
}
