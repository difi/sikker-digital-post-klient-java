package no.difi.sdp.client.domain;

import java.security.cert.X509Certificate;

public class Mottaker {

    private Mottaker(String personidentifikator, String postkasseadresse, X509Certificate mottakerSertifikat, String orgNummerMottakerVirksomhet) {
        this.personidentifikator = personidentifikator;
        this.postkasseadresse = postkasseadresse;
        this.mottakerSertifikat = mottakerSertifikat;
        this.orgNummerMottakerVirksomhet = orgNummerMottakerVirksomhet;
    }

    private String personidentifikator;
    private String postkasseadresse;
    private String mobilnummer;
    private String epostadresse;
    private X509Certificate mottakerSertifikat;

    /**
     * TODO: Denne fås fra oppslagsregisteret. Bør navgivningen ligge tettere opp til det den heter der? Vil gjøre det vesentlig lettere å bruke APIet…
     *
     * Identifikator (organisasjonsnummer) til virksomheten som er sluttmottaker i meldingsprosessen.
     *
     * Ved initiell sending av melding vil dette alltid være en postboks eller utskriftsleverandør.
     */
    private String orgNummerMottakerVirksomhet;

    public static Builder builder(String personidentifikator, String postkasseadresse, X509Certificate mottakerSertifikat, String orgNummerMottakerVirksomhet) {
        return new Builder(personidentifikator, postkasseadresse, mottakerSertifikat, orgNummerMottakerVirksomhet);
    }

    public static class Builder {
        private final Mottaker target;

        private Builder(String personidentifikator, String postkasseadresse, X509Certificate mottakerSertifikat, String orgNummerMottakerVirksomhet) {
            target = new Mottaker(personidentifikator, postkasseadresse, mottakerSertifikat, orgNummerMottakerVirksomhet);
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
            return target;
        }
    }
}
