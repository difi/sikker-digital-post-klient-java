package no.difi.sdp.client2.domain;

public interface AktørOrganisasjonsnummer {

    static AktørOrganisasjonsnummer of(String organisasjonsnummer) {
        return new EtOrganisasjonsnummer(organisasjonsnummer);
    }

    String getOrganisasjonsnummer();

    String getOrganisasjonsnummerMedLandkode();

    AvsenderOrganisasjonsnummer forfremTilAvsender();

    DatabehandlerOrganisasjonsnummer forfremTilDatabehandler();

}
