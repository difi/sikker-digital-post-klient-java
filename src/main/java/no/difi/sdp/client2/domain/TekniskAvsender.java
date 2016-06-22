package no.difi.sdp.client2.domain;

import no.digipost.api.representations.Organisasjonsnummer;

public class TekniskAvsender {

    public final Organisasjonsnummer organisasjonsnummer;
    public final Noekkelpar noekkelpar;

    private TekniskAvsender(Organisasjonsnummer organisasjonsnummer, Noekkelpar noekkelpar) {

        this.organisasjonsnummer = organisasjonsnummer;
        this.noekkelpar = noekkelpar;
    }

    /**
     * @param organisasjonsnummer Organisasjonsnummeret til avsender av brevet.
     * @param noekkelpar Avsenders nøkkelpar: signert virksomhetssertifikat og tilhørende privatnøkkel.
     */
    public static Builder builder(Organisasjonsnummer organisasjonsnummer, Noekkelpar noekkelpar) {
        return new Builder(organisasjonsnummer, noekkelpar);
    }

    public static class Builder {

        private final TekniskAvsender target;
        private boolean built = false;

        private Builder(Organisasjonsnummer organisasjonsnummer, Noekkelpar noekkelpar) {
            target = new TekniskAvsender(organisasjonsnummer, noekkelpar);
        }

        public TekniskAvsender build() {
            if (built) throw new IllegalStateException("Can't build twice");
            built = true;
            return this.target;
        }
    }
}
