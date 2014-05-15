package no.difi.sdp.client.domain;

import java.security.cert.X509Certificate;

public class Mottaker {

    private Mottaker(String personidentifikator, String postkasseadresse, X509Certificate mottakerSertifikat, String orgNummerPostkasse) {
        this.personidentifikator = personidentifikator;
        this.postkasseadresse = postkasseadresse;
        this.mottakerSertifikat = mottakerSertifikat;
        this.orgNummerPostkasse = orgNummerPostkasse;
    }

    private String personidentifikator;
    private String postkasseadresse;
    private X509Certificate mottakerSertifikat;
    private String orgNummerPostkasse;

    private String mobilnummer;
    private String epostadresse;

    /**
     * Informasjon om mottaker. Vil vanligvis være hentet fra <a href="http://begrep.difi.no/Oppslagstjenesten/">Oppslagstjenesten</a>.
     *
     * @param personidentifikator Identifikator (fødselsnummer) til mottaker av brevet.
     * @param postkasseadresse Mottakerens adresse hos postkasseleverandøren.
     * @param mottakerSertifikat Mottakers sertifikat.
     * @param orgNummerPostkasse Identifikator (organisasjonsnummer) til virksomheten som er sluttmottaker i meldingsprosessen.
     */
    public static Builder builder(String personidentifikator, String postkasseadresse, X509Certificate mottakerSertifikat, String orgNummerPostkasse) {
        return new Builder(personidentifikator, postkasseadresse, mottakerSertifikat, orgNummerPostkasse);
    }

    public static class Builder {
        private final Mottaker target;
        private boolean built = false;

        private Builder(String personidentifikator, String postkasseadresse, X509Certificate mottakerSertifikat, String orgNummerPostkasse) {
            target = new Mottaker(personidentifikator, postkasseadresse, mottakerSertifikat, orgNummerPostkasse);
        }

        public Builder epostadresse(String epostadresse) {
            target.epostadresse = epostadresse;
            return this;
        }

        public Builder mobilnummer(String mobilnummer) {
            target.mobilnummer = mobilnummer;
            return this;
        }

        public Mottaker build() {
            if (built) throw new IllegalStateException("Can't build twice");
            built = true;
            return target;
        }
    }
}
