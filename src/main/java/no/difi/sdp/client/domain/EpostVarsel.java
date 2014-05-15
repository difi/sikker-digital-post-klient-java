package no.difi.sdp.client.domain;

import java.util.ArrayList;
import java.util.List;

public class EpostVarsel extends Varsel {

    private String epostadresse;

    private EpostVarsel(String tekst) {
        super(tekst);
    }

    /**
     * @param tekst Avsenderstyrt tekst som skal inngå i varselet.
     */
    public static Builder builder(String tekst) {
        return new Builder(tekst);
    }

    public static class Builder {
        private EpostVarsel target;

        private Builder(String tekst) {
            target = new EpostVarsel(tekst);
        }

        /**
         * Antall dager etter brevet er tilgjengeliggjort for mottaker det første, andre osv varsel skal sendes.
         *
         * Eksempel: 0, 2, 5, 10
         * Hvis brevet blir tilgjengeliggjort 1.7.2014 vil det bli sendt varsel:
         * <ul>
         *     <li>1.7.2014</li>
         *     <li>3.7.2014</li>
         *     <li>6.7.2014</li>
         *     <li>11.7.2014</li>
         * </ul>
         *
         * Det vil ikke bli sendt flere varsler etter mottakeren har åpnet brevet.
         *
         * Standard er ett varsel samtidig som brevet blir tilgjengeliggjort for mottaker.
         */
        public Builder varselEtterDager(List<Integer> varselEtterDager) {
            target.dagerEtter = new ArrayList<Integer>(varselEtterDager);
            return this;
        }

        /**
         * Språkkode i henhold til ISO-639-1 (2 bokstaver). Brukes til å informere postkassen om hvilket språk som benyttes, slik at varselet om mulig kan vises i riktig språkkontekst.
         *
         * Standard er NO.
         */
        public Builder spraakkode(String spraakkode) {
            target.spraakkode = spraakkode;
            return this;
        }


        public Builder epostadresse(String epostadresse) {
            target.epostadresse = epostadresse;
            return this;
        }

        public EpostVarsel build() {
            return target;
        }
    }
}
