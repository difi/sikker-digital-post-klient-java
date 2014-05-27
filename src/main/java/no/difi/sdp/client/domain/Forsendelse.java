package no.difi.sdp.client.domain;

import no.difi.sdp.client.domain.digital_post.DigitalPost;
import no.difi.sdp.client.domain.fysisk_post.FysiskPost;

import java.util.UUID;

public class Forsendelse {

    private Forsendelse(DigitalPost digitalPost, FysiskPost fysiskPost, Dokumentpakke dokumentpakke) {
        if ((fysiskPost != null && digitalPost != null) || (fysiskPost == null && digitalPost == null)) {
            throw new IllegalArgumentException("Can only specify exactly one of digitalPost and fysiskPost");
        }

        this.digitalPost = digitalPost;
        this.fysiskPost = fysiskPost;
        this.dokumentpakke = dokumentpakke;
    }

    private DigitalPost digitalPost;
    private FysiskPost fysiskPost;
    private Dokumentpakke dokumentpakke;
    private String konversasjonsId = UUID.randomUUID().toString();
    private Prioritet prioritet = Prioritet.NORMAL;
    private String spraakkode = "NO";

    public String getKonversasjonsId() {
        return konversasjonsId;
    }

    public boolean isDigitalPostforsendelse() {
        return digitalPost != null;
    }

    public DigitalPost getDigitalPost() {
        return digitalPost;
    }

    public FysiskPost getFysiskPost() {
        return fysiskPost;
    }

    public Dokumentpakke getDokumentpakke() {
        return dokumentpakke;
    }

    public Prioritet getPrioritet() {
        return prioritet;
    }

    public String getSpraakkode() {
        return spraakkode;
    }

    /**
     * @param digitalPost Informasjon som brukes av postkasseleverandør for å behandle den digitale posten.
     */
    public static Builder builder(DigitalPost digitalPost, Dokumentpakke dokumentpakke) {
        return new Builder(digitalPost, null, dokumentpakke);
    }

    public static Builder builder(FysiskPost fysiskPost, Dokumentpakke dokumentpakke) {
        return new Builder(null, fysiskPost, dokumentpakke);
    }

    public static class Builder {

        private final Forsendelse target;
        private boolean built = false;

        private Builder(DigitalPost digitalPost, FysiskPost fysiskPost, Dokumentpakke dokumentpakke) {
            this.target = new Forsendelse(digitalPost, fysiskPost, dokumentpakke);
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

        /**
         * Språkkode i henhold til ISO-639-1 (2 bokstaver). Brukes til å informere postkassen om hvilket språk som benyttes, slik at varselet om mulig kan vises i riktig språkkontekst.
         *
         * Standard er NO.
         */
        public Builder spraakkode(String spraakkode) {
            target.spraakkode = spraakkode;
            return this;
        }


        public Forsendelse build() {
            if (built) throw new IllegalStateException("Can't build twice");
            built = true;
            return target;
        }
    }
}
