package no.difi.sdp.client.domain;

import java.util.UUID;

public class Forsendelse {

    private Forsendelse(DigitalpostInfo digitalpostInfo, Dokumentpakke dokumentpakke, Mottaker mottaker) {
        this.digitalpostInfo = digitalpostInfo;
        this.dokumentpakke = dokumentpakke;
        this.mottaker = mottaker;
    }

    /**
     * ID for forsendelsen. Skal være unik for en avsender.
     *
     * Standard er {@link java.util.UUID#randomUUID()}}.
     */
    private String konversasjonsId = UUID.randomUUID().toString();

    /**
     * Standard er {@link no.difi.sdp.client.domain.Prioritet#NORMAL}
     */
    private Prioritet prioritet = Prioritet.NORMAL;

    private Mottaker mottaker;

    /**
     * Informasjon som brukes av postkasseleverandør for å behandle den digitale posten.
     */
    private DigitalpostInfo digitalpostInfo;

    private Dokumentpakke dokumentpakke;

    public static Builder builder(DigitalpostInfo digitalpostInfo, Dokumentpakke dokumentpakke, Mottaker mottaker) {
        return new Builder(digitalpostInfo, dokumentpakke, mottaker);
    }

    public static class Builder {

        private final Forsendelse target;

        public Builder(DigitalpostInfo digitalpostInfo, Dokumentpakke dokumentpakke, Mottaker mottaker) {
            this.target = new Forsendelse(digitalpostInfo, dokumentpakke, mottaker);
        }

        public Builder konversasjonsId(String konversasjonsId) {
            target.konversasjonsId = konversasjonsId;
            return this;
        }

        public Builder prioritet(Prioritet prioritet) {
            target.prioritet = prioritet;
            return this;
        }

        public Forsendelse build() {
            return target;
        }
    }
}
