package no.difi.sdp.client.domain;

public class Forsendelse {

    private String konversasjonsId;
    private Prioritet prioritet;
    private Mottaker mottaker;

    /**
     * Identifikator (organisasjonsnummer) til virksomheten som er sluttmottaker i
     * meldingsprosessen. Ved initiell sending av melding vil dette alltid være en postboks eller utskriftsleverandør.
     */
    private String orgNummerMottakerVirksomhet;

    /**
     * Informasjon som brukes av postkasseleverandør for å behandle den digitale posten.
     */
    private DigitalpostInfo digitalpostInfo;

    /**
     * todo: SBD/Melding/Dokumentpakke --> hvorfor samme begrep som selve vedlegget? Hvor finner vi engangsnøkkel?
     */

    /**
     * todo: SBD/Melding/FysiskpostInfo --> trengs denne? Hva må vi få inn?
     */

    private Dokumentpakke dokumentpakke;

}
