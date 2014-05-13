package no.difi.sdp.client.domain;

import java.util.List;

public class Varsel {

    /**
     * Avsenderstyrt tekst som skal inngå i e-postvarselet.
     */
    private Varselstekst varseltekst;

    /**
     * Antall dager etter brevet er tilgjengeliggjort for mottaker det første, andre osv varselet skal sendes.
     */
    private List<Integer> dagerEtter;

}
