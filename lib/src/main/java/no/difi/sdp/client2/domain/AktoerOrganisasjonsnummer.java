package no.difi.sdp.client2.domain;

import no.digipost.api.representations.Organisasjonsnummer;

public interface AktoerOrganisasjonsnummer {

    static AktoerOrganisasjonsnummer of(String organisasjonsnummer) {
        return new EtOrganisasjonsnummer(organisasjonsnummer);
    }

    static AktoerOrganisasjonsnummer of(Organisasjonsnummer organisasjonsnummer) {
        return new EtOrganisasjonsnummer(organisasjonsnummer.getOrganisasjonsnummer());
    }

    String getOrganisasjonsnummer();

    String getOrganisasjonsnummerMedLandkode();

    AvsenderOrganisasjonsnummer forfremTilAvsender();

    DatabehandlerOrganisasjonsnummer forfremTilDatabehandler();

}
