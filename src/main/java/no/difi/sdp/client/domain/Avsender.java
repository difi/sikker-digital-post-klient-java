package no.difi.sdp.client.domain;

import java.security.cert.X509Certificate;

public class Avsender {

    private X509Certificate avsenderSertifikat;

    /**
     * Identifikator (organisasjonsnummer) til virksomheten som initierer (er avsender)
     * i meldingsprosessen. Alle kvitteringer skal addresseres til denne parten som mottaker.
     */
    private String orgNummer;

    private Rolle rolle;


}
