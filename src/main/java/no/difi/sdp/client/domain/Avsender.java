package no.difi.sdp.client.domain;

import no.posten.dpost.offentlig.api.interceptors.KeyStoreInfo;

import java.security.PrivateKey;

public class Avsender {

    private Avsender(String organisasjonsnummer, Sertifikat sertifikat, PrivateKey privatnoekkel) {
        this.sertifikat = sertifikat;
        this.organisasjonsnummer = organisasjonsnummer;
        this.privatnoekkel = privatnoekkel;
    }

    private Sertifikat sertifikat;
    private PrivateKey privatnoekkel;

    private String organisasjonsnummer;
    private String avsenderIdentifikator;
    private String fakturaReferanse;
    private String orgNummerDatabehandler;

    private AvsenderRolle rolle = AvsenderRolle.BEHANDLINGSANSVARLIG;

    /**
     * @param organisasjonsnummer Identifikator (organisasjonsnummer) til virksomheten som initierer (er avsender) i meldingsprosessen.
     * @param sertifikat Avsenders virksomhetssertifikat.
     * @param privatnoekkel Den private nøkkelen som tilsvarer den offentlige nøkkelen i avsenders virksomhetssertifikat.
     */
    public static Builder builder(String organisasjonsnummer, Sertifikat sertifikat, PrivateKey privatnoekkel) {
        return new Builder(organisasjonsnummer, sertifikat, privatnoekkel);
    }

    public String getOrganisasjonsnummer() {
        return organisasjonsnummer;
    }

    public KeyStoreInfo getKeyStore() {
        return null;
    }

    public static class Builder {

        private final Avsender target;
        private boolean built = false;

        private Builder(String orgNummer, Sertifikat sertifikat, PrivateKey privatnoekkel) {
            target = new Avsender(orgNummer, sertifikat, privatnoekkel);
        }

        /**
         * Rollen til utførende avsender i henhold til <a href="http://begrep.difi.no/SikkerDigitalPost/Aktorer">http://begrep.difi.no/SikkerDigitalPost/Aktorer</a>.
         *
         * Standard er {@link AvsenderRolle#BEHANDLINGSANSVARLIG}
         */
        public Builder rolle(AvsenderRolle rolle) {
            target.rolle = rolle;
            return this;
        }

        public Builder fakturaReferanse(String fakturaReferanse) {
            target.fakturaReferanse = fakturaReferanse;
            return this;
        }

        /**
         * Brukt for å identifisere en ansvarlig enhet innen for en virksomhet.
         *
         * @param avsenderIdentifikator Identifikator som er tildelt av Sikker digital posttjeneste ved tilkobling til tjenesten.
         */
        public Builder avsenderIdentifikator(String avsenderIdentifikator) {
            target.avsenderIdentifikator = avsenderIdentifikator;
            return this;
        }

        /**
         * @param orgNummerDatabehandler Identifikator (organisasjonsnummer) til avsender eller avtalepart hos avsender, ansvarlig for pakking og sikring av postforsendelser.
         */
        public Builder orgNummerDatabehandler(String orgNummerDatabehandler) {
            target.orgNummerDatabehandler = orgNummerDatabehandler;
            return this;
        }

        public Avsender build() {
            if (built) throw new IllegalStateException("Can't build twice");
            built = true;
            return this.target;
        }
    }
}
