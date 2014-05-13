package no.difi.sdp.client.domain;

public class MottakerVirksomhet {

    /**
     * Identifikator (organisasjonsnummer) til virksomheten som er sluttmottaker i
     * meldingsprosessen. Ved initiell sending av melding vil dette alltid være en postboks eller utskriftsleverandør.
     */
    private String orgNummer;

    private Rolle rolle;
}
