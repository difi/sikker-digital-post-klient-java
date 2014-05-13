package no.difi.sdp.client.domain;

import java.util.Date;

public class Forsendelse {

    private String konversasjonsId;

    private Mottaker mottaker;

    private Dokumentpakke dokumentpakke;

    /**
     * Når brevet tilgjengeliggjøres for mottaker. Standard er nå.
     */
    private Date virkningsdato = new Date();

    private boolean aapningskvittering;

    /**
     * Sikkerhetsnivå som kreves for å åpne brevet. Standard er nivå 3 (passord).
     */
    private Sikkerhetsnivaa sikkerhetsnivaa = Sikkerhetsnivaa.NIVAA_3;

    /**
     * Ikke-sensitiv tittel på brevet.
     */
    private String tittel;

    /**
     * Varsler som skal sendes til mottaker av brevet.
     */
    private Varsler varsler;

    /**
     * Identifikator (organisasjonsnummer) til virksomheten som initierer (er avsender)
     * i meldingsprosessen. Alle kvitteringer skal addresseres til denne parten som mottaker.
     * Er dette samme som avsender?
     */
    private String organisasjonsNummerSender;

    /**
     * Identifikator (organisasjonsnummer) til virksomheten som er sluttmottaker i
     * meldingsprosessen. Ved initiell sending av melding vil dette alltid være en postboks eller utskriftsleverandør.
     */
    private String organisasjonsNummerReceiver;

}
