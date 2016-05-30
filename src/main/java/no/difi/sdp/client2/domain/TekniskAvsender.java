package no.difi.sdp.client2.domain;

public class TekniskAvsender {

    public final String organisasjonsnummer;
    public final Noekkelpar noekkelpar;

    private TekniskAvsender(String organisasjonsnummer, Noekkelpar noekkelpar) {
        this.organisasjonsnummer = organisasjonsnummer;
        this.noekkelpar = noekkelpar;
    }

    /**
     * @param organisasjonsnummer Organisasjonsnummeret til avsender av brevet.
     * @param noekkelpar Avsenders nøkkelpar: signert virksomhetssertifikat og tilhørende privatnøkkel.
     */
    public static Builder builder(String organisasjonsnummer, Noekkelpar noekkelpar) {
        return new Builder(organisasjonsnummer, noekkelpar);
    }

    public static class Builder {

        private final TekniskAvsender target;
        private boolean built = false;

        private Builder(String orgNummer, Noekkelpar noekkelpar) {
            target = new TekniskAvsender(orgNummer, noekkelpar);
        }

        public TekniskAvsender build() {
            if (built) throw new IllegalStateException("Can't build twice");
            built = true;
            return this.target;
        }
    }
}
