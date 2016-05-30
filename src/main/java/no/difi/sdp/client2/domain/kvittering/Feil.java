package no.difi.sdp.client2.domain.kvittering;

import no.digipost.api.representations.EbmsApplikasjonsKvittering;

public class Feil extends ForretningsKvittering {

    private Feiltype feiltype;
    private String detaljer;

    private Feil(EbmsApplikasjonsKvittering applikasjonsKvittering, Feiltype feiltype) {
        super(applikasjonsKvittering);
        this.feiltype = feiltype;
    }

    public Feiltype getFeiltype() {
        return feiltype;
    }

    public String getDetaljer() {
        return detaljer;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "konversasjonsId=" + getKonversasjonsId() +
                ", feiltype=" + feiltype +
                ", detaljer='" + detaljer + '\'' +
                '}';
    }

    public static Builder builder(EbmsApplikasjonsKvittering applikasjonsKvittering, Feiltype feiltype) {
        return new Builder(applikasjonsKvittering, feiltype);
    }

    public static class Builder {
        private Feil target;
        private boolean built = false;

        public Builder(EbmsApplikasjonsKvittering applikasjonsKvittering, Feiltype feiltype) {
            target = new Feil(applikasjonsKvittering, feiltype);
        }

        public Builder detaljer(String detaljer) {
            target.detaljer = detaljer;
            return this;
        }

        public Feil build() {
            if (built) throw new IllegalStateException("Can't build twice");
            built = true;
            return target;
        }

    }

    public enum Feiltype {

        /**
         * Feil som har oppstått som følge av en feil hos klienten.
         */
        KLIENT,

        /**
         * Feil som har oppstått som følge av feil hos klienten. Bør meldes til sentralforvalter.
         */
        SERVER
    }

}
