package no.difi.sdp.client.domain;

public class KvitteringForespoersel {

    /**
     * Hvilken prioritet det forespørres kvittering for.
     *
     * Det må eksplisitt forespørres kvitteringer for en gitt prioritet. Dersom det gjøres en prioritert forsendelse vil man aldri få kvittering dersom man spør etter kvittering på normale.
     */
    private Prioritet prioritet;

}
