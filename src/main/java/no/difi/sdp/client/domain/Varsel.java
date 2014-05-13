package no.difi.sdp.client.domain;

public class Varsel {

    /**
     * Avsenderstyrt tekst som skal inngå i e-postvarselet.
     */
    private Varselstekst varseltekst;

    /**
     * Antall ganger e-postvarsel skal sendes dersom mottaker ikke åpner brevet.
     */
    private int repitisjoner = 1;

    /**
     * Antall dager etter brevet er tilgjengeliggjort for mottaker det første varselet skal sendes.
     */
    private int dagerEtter = 0;

}
