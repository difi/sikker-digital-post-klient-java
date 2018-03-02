package no.difi.sdp.client2.domain.utvidelser;


import no.difi.begrep.sdp.utvidelser.lenke.SDPLenke;
import no.difi.begrep.sdp.utvidelser.lenke.SDPLenkeBeskrivelseTekst;
import no.difi.begrep.sdp.utvidelser.lenke.SDPLenkeKnappTekst;

import java.time.ZonedDateTime;

import static no.digipost.api.xml.Schemas.LENKE_SCHEMA;

public final class Lenke extends DataDokument {

    private final String url;
    private String beskrivelse;
    private String knappetekst;
    private String spraakkode = "NO";
    private ZonedDateTime frist;


    private Lenke(String filnavn, String url) {
        super(filnavn, "application/vnd.difi.dpi.lenke+xml", SDPLenke.class, LENKE_SCHEMA);
        this.url = url;
    }

    @Override
    SDPLenke jaxbObject() {
        return new SDPLenke(
                url,
                beskrivelse != null ? new SDPLenkeBeskrivelseTekst(beskrivelse, spraakkode) : null,
                knappetekst != null ? new SDPLenkeKnappTekst(knappetekst, spraakkode) : null,
                frist
        );
    }

    public static Builder builder(String filnavn, String url) {
        return new Builder(filnavn, url);
    }

    public static final class Builder {

        private final Lenke target;
        private boolean built = false;

        private Builder(String filnavn, String url) {
            target = new Lenke(filnavn, url);
        }

        public Builder beskrivelse(String beskrivelse) {
            target.beskrivelse = beskrivelse;
            return this;
        }

        public Builder knappetekst(String knappetekst) {
            target.knappetekst = knappetekst;
            return this;
        }

        /**
         * Språkkode i henhold til ISO-639-1 (2 bokstaver). Brukes til å informere postkassen om hvilket språk som benyttes.
         * <p>
         * Standard er NO.
         */
        public Builder spraakkode(String spraakkode) {
            target.spraakkode = spraakkode;
            return this;
        }

        public Builder frist(ZonedDateTime frist) {
            target.frist = frist;
            return this;
        }

        public Lenke build() {
            if (built) throw new IllegalStateException("Can't build twice");
            built = true;
            return target;
        }
    }
}
