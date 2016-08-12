package no.difi.sdp.client2.domain;

import no.digipost.api.representations.Organisasjonsnummer;

public interface AktørOrganisasjonsnummer {

    static AktørOrganisasjonsnummer of(String organisasjonsnummer) {
        return new EtOrganisasjonsnummer(organisasjonsnummer);
    }

    static AktørOrganisasjonsnummer of(Organisasjonsnummer organisasjonsnummer) {
        return new EtOrganisasjonsnummer(organisasjonsnummer.getOrganisasjonsnummer());
    }

    String getOrganisasjonsnummer();

    String getOrganisasjonsnummerMedLandkode();

    AvsenderOrganisasjonsnummer forfremTilAvsender();

    DatabehandlerOrganisasjonsnummer forfremTilDatabehandler();

}
