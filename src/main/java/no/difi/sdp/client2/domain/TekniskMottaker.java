package no.difi.sdp.client2.domain;

public class TekniskMottaker {

	public final String organisasjonsnummer;
	public final Sertifikat sertifikat;

	public TekniskMottaker(String organisasjonsnummer, Sertifikat sertifikat) {
	    this.organisasjonsnummer = organisasjonsnummer;
	    this.sertifikat = sertifikat;
    }

}
