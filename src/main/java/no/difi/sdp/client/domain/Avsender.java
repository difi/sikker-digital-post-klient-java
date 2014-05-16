package no.difi.sdp.client.domain;

public class Avsender {

    private Avsender(String organisasjonsnummer, Noekkelpar noekkelpar) {
        this.organisasjonsnummer = organisasjonsnummer;
        this.noekkelpar = noekkelpar;
    }

    private String organisasjonsnummer;
    private Noekkelpar noekkelpar;
    private String avsenderIdentifikator;
    private String fakturaReferanse;
    private String orgNummerDatabehandler;

    private AvsenderRolle rolle = AvsenderRolle.BEHANDLINGSANSVARLIG;

    public String getOrganisasjonsnummer() {
        return organisasjonsnummer;
    }

    public Noekkelpar getNoekkelpar() {
        return noekkelpar;
    }

    /**
     * @param organisasjonsnummer Identifikator (organisasjonsnummer) til virksomheten som initierer (er avsender) i meldingsprosessen.
     * @param noekkelpar Avsenders nøkkelpar: signert virksomhetssertifikat og tilhørende privatnøkkel.
     */
    public static Builder builder(String organisasjonsnummer, Noekkelpar noekkelpar) {
        return new Builder(organisasjonsnummer, noekkelpar);
    }

    public static class Builder {

        private final Avsender target;
        private boolean built = false;

        private Builder(String orgNummer, Noekkelpar noekkelpar) {
            target = new Avsender(orgNummer, noekkelpar);
        }

        /**
         * Rollen til utførende avsender i henhold til <a href="http://begrep.difi.no/SikkerDigitalPost/Aktorer">http://begrep.difi.no/SikkerDigitalPost/Aktorer</a>.
         *
         * Standard er {@link AvsenderRolle#BEHANDLINGSANSVARLIG}
         */
        public Builder rolle(AvsenderRolle rolle) {
            target.rolle = rolle;
            return this;
        }

        public Builder fakturaReferanse(String fakturaReferanse) {
            target.fakturaReferanse = fakturaReferanse;
            return this;
        }

        /**
         * Brukt for å identifisere en ansvarlig enhet innen for en virksomhet.
         *
         * @param avsenderIdentifikator Identifikator som er tildelt av Sikker digital posttjeneste ved tilkobling til tjenesten.
         */
        public Builder avsenderIdentifikator(String avsenderIdentifikator) {
            target.avsenderIdentifikator = avsenderIdentifikator;
            return this;
        }

        /**
         * @param orgNummerDatabehandler Identifikator (organisasjonsnummer) til avsender eller avtalepart hos avsender, ansvarlig for pakking og sikring av postforsendelser.
         */
        public Builder orgNummerDatabehandler(String orgNummerDatabehandler) {
            target.orgNummerDatabehandler = orgNummerDatabehandler;
            return this;
        }

        public Avsender build() {
            if (built) throw new IllegalStateException("Can't build twice");
            built = true;
            return this.target;
        }
    }
}
