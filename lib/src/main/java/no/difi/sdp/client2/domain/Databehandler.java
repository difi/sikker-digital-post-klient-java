package no.difi.sdp.client2.domain;

public class Databehandler {

    public final DatabehandlerOrganisasjonsnummer organisasjonsnummer;
    public final Noekkelpar noekkelpar;

    private Databehandler(DatabehandlerOrganisasjonsnummer organisasjonsnummer, Noekkelpar noekkelpar) {
        this.organisasjonsnummer = organisasjonsnummer;
        this.noekkelpar = noekkelpar;
    }

    /**
     * @param organisasjonsnummer Organisasjonsnummeret til avsender av brevet.
     * @param noekkelpar          Avsenders nøkkelpar: signert virksomhetssertifikat og tilhørende privatnøkkel.
     */
    public static Builder builder(DatabehandlerOrganisasjonsnummer organisasjonsnummer, Noekkelpar noekkelpar) {
        return new Builder(organisasjonsnummer, noekkelpar);
    }

    public static class Builder {

        private final Databehandler target;
        private boolean built = false;

        private Builder(DatabehandlerOrganisasjonsnummer organisasjonsnummer, Noekkelpar noekkelpar) {
            target = new Databehandler(organisasjonsnummer, noekkelpar);
        }

        public Databehandler build() {
            if (built) throw new IllegalStateException("Can't build twice");
            built = true;
            return this.target;
        }
    }
}
