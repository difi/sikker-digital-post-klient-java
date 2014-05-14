package no.difi.sdp.client.domain;

import java.util.Date;

public class DigitalpostInfo {

    private DigitalpostInfo(String tittel) {
        this.tittel = tittel;
    }

    /**
     * Når brevet tilgjengeliggjøres for mottaker. Standard er nå.
     */
    private Date virkningsdato = new Date();

    /**
     * Ønskes kvittering når brevet blir åpnet av mottaker? Standard er false.
     */
    private boolean aapningskvittering = false;

    /**
     * Sikkerhetsnivå som kreves for å åpne brevet. Standard er {@link Sikkerhetsnivaa#NIVAA_3} (passord).
     */
    private Sikkerhetsnivaa sikkerhetsnivaa = Sikkerhetsnivaa.NIVAA_3;

    /**
     * Ikke-sensitiv tittel på brevet.
     */
    private String tittel;

    /**
     * Varsler som skal sendes til mottaker av brevet. Standard er e-postvarsel med standardteksten til postkasseleverandøren.
     */
    private Varsler varsler = Varsler.builder().epostVarsel(Varsel.builder().build()).build();

    public static Builder builder(String ikkeSensitivTittel) {
        return new Builder(ikkeSensitivTittel);
    }

    public static class Builder {

        private final DigitalpostInfo target;

        public Builder(String ikkeSensitivTittel) {
            target = new DigitalpostInfo(ikkeSensitivTittel);
        }

        public Builder virkningsdato(Date virkningsdato) {
            target.virkningsdato = virkningsdato;
            return this;
        }

        public Builder aapningskvittering(boolean aapningskvittering) {
            target.aapningskvittering = aapningskvittering;
            return this;
        }

        public Builder sikkerhetsnivaa(Sikkerhetsnivaa sikkerhetsnivaa) {
            target.sikkerhetsnivaa = sikkerhetsnivaa;
            return this;
        }

        public Builder varsler(Varsler varsler) {
            target.varsler = varsler;
            return this;
        }

        public DigitalpostInfo build() {
            return target;
        }
    }
}
