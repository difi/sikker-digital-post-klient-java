package no.difi.sdp.client.domain;

public class Varselstekst {

    private Varselstekst(String tekst) {
        this.tekst = tekst;
    }

    /**
     * Språkkode i henhold til ISO-639-1 (2 bokstaver). Brukes til å informere postkassen om hvilket språk som benyttes, slik at varselet om mulig kan vises i riktig språkkontekst.
     *
     * Standard er NO.
     */
    private String spraakkode = "NO";
    private String tekst;

    public static Builder builder(String tekst) {
        return new Builder(tekst);
    }

    public static class Builder {

        private final Varselstekst target;

        public Builder(String tekst) {
            target = new Varselstekst(tekst);
        }

        public Builder spraakkode(String spraakkode) {
            target.spraakkode = spraakkode;
            return this;
        }

        public Varselstekst build() {
            return target;
        }
    }
}
