package no.difi.sdp.client.domain;

public class Mottaker {

    private Mottaker(String personidentifikator, String postkasseadresse, Sertifikat mottakerSertifikat, String orgNummerPostkasse) {
        this.personidentifikator = personidentifikator;
        this.postkasseadresse = postkasseadresse;
        this.mottakerSertifikat = mottakerSertifikat;
        this.orgNummerPostkasse = orgNummerPostkasse;
    }

    private String personidentifikator;
    private String postkasseadresse;
    private Sertifikat mottakerSertifikat;
    private String orgNummerPostkasse;

    /**
     * Informasjon om mottaker. Vil vanligvis være hentet fra <a href="http://begrep.difi.no/Oppslagstjenesten/">Oppslagstjenesten</a>.
     *
     * @param personidentifikator Identifikator (fødselsnummer) til mottaker av brevet.
     * @param postkasseadresse Mottakerens adresse hos postkasseleverandøren.
     * @param mottakerSertifikat Mottakers sertifikat.
     * @param orgNummerPostkasse Identifikator (organisasjonsnummer) til virksomheten som er sluttmottaker i meldingsprosessen.
     */
    public static Builder builder(String personidentifikator, String postkasseadresse, Sertifikat mottakerSertifikat, String orgNummerPostkasse) {
        return new Builder(personidentifikator, postkasseadresse, mottakerSertifikat, orgNummerPostkasse);
    }

    public static class Builder {
        private final Mottaker target;
        private boolean built = false;

        private Builder(String personidentifikator, String postkasseadresse, Sertifikat mottakerSertifikat, String orgNummerPostkasse) {
            target = new Mottaker(personidentifikator, postkasseadresse, mottakerSertifikat, orgNummerPostkasse);
        }

        public Mottaker build() {
            if (built) throw new IllegalStateException("Can't build twice");
            built = true;
            return target;
        }
    }
}
