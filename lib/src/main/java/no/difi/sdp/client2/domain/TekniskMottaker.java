package no.difi.sdp.client2.domain;

import no.digipost.api.representations.Organisasjonsnummer;

public class TekniskMottaker {

	public final Organisasjonsnummer organisasjonsnummer;
	public final Sertifikat sertifikat;

	public TekniskMottaker(Organisasjonsnummer organisasjonsnummer, Sertifikat sertifikat) {
	    this.organisasjonsnummer = organisasjonsnummer;
	    this.sertifikat = sertifikat;
    }

}
