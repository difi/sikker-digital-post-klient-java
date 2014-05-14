package no.difi.sdp.client.domain;

import java.security.cert.X509Certificate;

public class Avsender {

    private Avsender(String orgNummer, X509Certificate sertifikat) {
        this.sertifikat = sertifikat;
        this.orgNummer = orgNummer;
    }

    private X509Certificate sertifikat;

    /**
     * Identifikator (organisasjonsnummer) til virksomheten som initierer (er avsender)
     * i meldingsprosessen. Alle kvitteringer skal addresseres til denne parten som mottaker.
     */
    private String orgNummer;

    /**
     * Rollen til utførende avsender i henhold til <a href="http://begrep.difi.no/SikkerDigitalPost/Aktorer">http://begrep.difi.no/SikkerDigitalPost/Aktorer</a>.
     *
     * Standard er {@link AvsenderRolle#BEHANDLINGSANSVARLIG}
     */
    private AvsenderRolle rolle = AvsenderRolle.BEHANDLINGSANSVARLIG;


    public static Builder builder(String orgNummer, X509Certificate sertifikat) {
        return new Builder(orgNummer, sertifikat);
    }

    public static class Builder {

        private final Avsender target;

        public Builder(String orgNummer, X509Certificate sertifikat) {
            this.target = new Avsender(orgNummer, sertifikat);
        }

        public Builder rolle(AvsenderRolle rolle) {
            this.target.rolle = rolle;
            return this;
        }

        public Avsender build() {
            return this.target;
        }
    }
}
