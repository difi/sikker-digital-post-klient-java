package no.difi.sdp.client.domain;

import java.util.UUID;

public class Forsendelse {

    private Forsendelse(DigitalpostInfo digitalpostInfo, Dokumentpakke dokumentpakke, Mottaker mottaker) {
        this.digitalpostInfo = digitalpostInfo;
        this.dokumentpakke = dokumentpakke;
        this.mottaker = mottaker;
    }

    private DigitalpostInfo digitalpostInfo;
    private Dokumentpakke dokumentpakke;
    private Mottaker mottaker;
    private String konversasjonsId = UUID.randomUUID().toString();
    private Prioritet prioritet = Prioritet.NORMAL;

    /**
     * @param digitalpostInfo Informasjon som brukes av postkasseleverandør for å behandle den digitale posten.
     */
    public static Builder builder(DigitalpostInfo digitalpostInfo, Dokumentpakke dokumentpakke, Mottaker mottaker) {
        return new Builder(digitalpostInfo, dokumentpakke, mottaker);
    }

    public static class Builder {

        private final Forsendelse target;

        private Builder(DigitalpostInfo digitalpostInfo, Dokumentpakke dokumentpakke, Mottaker mottaker) {
            this.target = new Forsendelse(digitalpostInfo, dokumentpakke, mottaker);
        }

        /**
         * ID for forsendelsen. Skal være unik for en avsender.
         *
         * Standard er {@link java.util.UUID#randomUUID()}}.
         */
        public Builder konversasjonsId(String konversasjonsId) {
            target.konversasjonsId = konversasjonsId;
            return this;
        }

        /**
         * Standard er {@link no.difi.sdp.client.domain.Prioritet#NORMAL}
         */
        public Builder prioritet(Prioritet prioritet) {
            target.prioritet = prioritet;
            return this;
        }

        public Forsendelse build() {
            return target;
        }
    }
}
