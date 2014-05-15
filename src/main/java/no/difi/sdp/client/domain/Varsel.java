package no.difi.sdp.client.domain;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class Varsel {

    private Varsel(String tekst) {
        this.tekst = tekst;
    }

    /**
     * Avsenderstyrt tekst som skal inngå i e-postvarselet.
     *
     * Standard er postkassens vanlige oppsett for varsler.
     */
    private String tekst;

    /**
     * Språkkode i henhold til ISO-639-1 (2 bokstaver). Brukes til å informere postkassen om hvilket språk som benyttes, slik at varselet om mulig kan vises i riktig språkkontekst.
     *
     * Standard er NO.
     */
    private String spraakkode = "NO";

    /**
     * Antall dager etter brevet er tilgjengeliggjort for mottaker det første, andre osv varsel skal sendes.
     *
     * Standard er ett varsel samtidig som brevet blir tilgjengeliggjort for mottaker.
     *
     * Maksimalt 10 varsler.
     */
    private List<Integer> dagerEtter = asList(0);

    public static Builder builder(String tekst) {
        return new Builder(tekst);
    }

    public static class Builder {

        private final Varsel target;

        private Builder(String tekst) {
            target = new Varsel(tekst);
        }

        public Builder varselEtterDager(List<Integer> varselEtterDager) {
            target.dagerEtter = new ArrayList<Integer>(varselEtterDager);
            return this;
        }

        public Builder spraakkode(String spraakkode) {
            target.spraakkode = spraakkode;
            return this;
        }

        public Varsel build() {
            return target;
        }
    }
}
