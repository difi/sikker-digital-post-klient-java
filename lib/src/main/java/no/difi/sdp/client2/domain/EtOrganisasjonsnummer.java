package no.difi.sdp.client2.domain;

import no.digipost.api.representations.Organisasjonsnummer;

public class EtOrganisasjonsnummer implements AvsenderOrganisasjonsnummer, DatabehandlerOrganisasjonsnummer {

    private Organisasjonsnummer organisasjonsnummer;

    EtOrganisasjonsnummer(String organisasjonsnummer) {
        this.organisasjonsnummer = Organisasjonsnummer.of(organisasjonsnummer);
    }

    @Override
    public String getOrganisasjonsnummer() {
        return organisasjonsnummer.getOrganisasjonsnummer();
    }

    @Override
    public String getOrganisasjonsnummerMedLandkode() {
        return organisasjonsnummer.getOrganisasjonsnummerMedLandkode();
    }

    @Override
    public AvsenderOrganisasjonsnummer forfremTilAvsender() {
        return this;
    }

    @Override
    public DatabehandlerOrganisasjonsnummer forfremTilDatabehandler() {
        return this;
    }

    @Override
    public String toString() {
        return organisasjonsnummer.toString();
    }
}
