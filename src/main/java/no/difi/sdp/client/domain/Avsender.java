package no.difi.sdp.client.domain;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

public class Avsender {

    private Avsender(String orgNummer, X509Certificate sertifikat, PrivateKey privatnoekkel) {
        this.sertifikat = sertifikat;
        this.orgNummer = orgNummer;
        this.privatnoekkel = privatnoekkel;
    }

    private X509Certificate sertifikat;
    private PrivateKey privatnoekkel;

    private String orgNummer;
    private String avsenderIdentifikator;
    private String fakturaReferanse;

    private AvsenderRolle rolle = AvsenderRolle.BEHANDLINGSANSVARLIG;

    /**
     * @param orgNummer Identifikator (organisasjonsnummer) til virksomheten som initierer (er avsender) i meldingsprosessen.
     * @param sertifikat Avsenders virksomhetssertifikat.
     * @param privatnoekkel Den private nøkkelen som tilsvarer den offentlige nøkkelen i avsenders virksomhetssertifikat.
     */
    public static Builder builder(String orgNummer, X509Certificate sertifikat, PrivateKey privatnoekkel) {
        return new Builder(orgNummer, sertifikat, privatnoekkel);
    }

    public static class Builder {

        private final Avsender target;
        private boolean built = false;

        private Builder(String orgNummer, X509Certificate sertifikat, PrivateKey privatnoekkel) {
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
         * @return
         */
        public Builder avsenderIdentifikator(String avsenderIdentifikator) {
            target.avsenderIdentifikator = avsenderIdentifikator;
            return this;
        }

        public Avsender build() {
            if (built) throw new IllegalStateException("Can't build twice");
            built = true;
            return this.target;
        }
    }
}
