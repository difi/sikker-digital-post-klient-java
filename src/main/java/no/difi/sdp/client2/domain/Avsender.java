package no.difi.sdp.client2.domain;

import no.digipost.api.representations.Organisasjonsnummer;

/**
 * Avsender som beskrevet i <a href="http://begrep.difi.no/SikkerDigitalPost/forretningslag/Aktorer">oversikten over aktører</a>.
 */
public class Avsender {

    private final Organisasjonsnummer organisasjonsnummer;
    private String avsenderIdentifikator;
    private String fakturaReferanse;

    public Avsender(Organisasjonsnummer organisasjonsnummer) {
        this.organisasjonsnummer = organisasjonsnummer;
    }

    public String getAvsenderIdentifikator() {
        return avsenderIdentifikator;
    }

    public String getFakturaReferanse() {
        return fakturaReferanse;
    }

    public Organisasjonsnummer getOrganisasjonsnummer() {
        return organisasjonsnummer;
    }

    public static Builder builder(Organisasjonsnummer organisasjonsnummer) {
        return new Builder(organisasjonsnummer);
    }

    public static class Builder {

        private final Avsender target;
        private boolean built = false;

        private Builder(Organisasjonsnummer organisasjonsnummer) {
            target = new Avsender(organisasjonsnummer);
        }

        public Builder fakturaReferanse(String fakturaReferanse) {
            target.fakturaReferanse = fakturaReferanse;
            return this;
        }

        /**
         * Brukes for å identifisere en ansvarlig enhet innen for en virksomhet. Benyttes dersom det er behov for å skille mellom ulike enheter hos avsender.
         *
         * @param avsenderIdentifikator Identifikator som er tildelt av Sentralforvalter ved tilkobling til tjenesten.
         */
        public Builder avsenderIdentifikator(String avsenderIdentifikator) {
            target.avsenderIdentifikator = avsenderIdentifikator;
            return this;
        }

        public Avsender build() {
            if (built) throw new IllegalStateException("Can't build twice");
            built = true;
            return this.target;
        }
    }

}
