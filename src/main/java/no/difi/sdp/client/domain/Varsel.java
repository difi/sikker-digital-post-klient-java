package no.difi.sdp.client.domain;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class Varsel {

    private Varsel() {}

    /**
     * Avsenderstyrt tekst som skal inngå i e-postvarselet.
     *
     * Standard er postkassens vanlige oppsett for varsler.
     */
    private Varselstekst varseltekst;

    /**
     * Antall dager etter brevet er tilgjengeliggjort for mottaker det første, andre osv varsel skal sendes.
     *
     * Standard er ett varsel samtidig som brevet blir tilgjengeliggjort for mottaker.
     *
     * Maksimalt 10 varsler.
     */
    private List<Integer> dagerEtter = asList(0);

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final Varsel target;

        private Builder() {
            target = new Varsel();
        }

        public Builder varselEtterDager(List<Integer> varselEtterDager) {
            target.dagerEtter = new ArrayList<Integer>(varselEtterDager);
            return this;
        }

        public Builder varseltekst(Varselstekst varseltekst) {
            target.varseltekst = varseltekst;
            return this;
        }

        public Varsel build() {
            return target;
        }
    }
}
