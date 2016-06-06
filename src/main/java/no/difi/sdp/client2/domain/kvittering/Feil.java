package no.difi.sdp.client2.domain.kvittering;

import no.digipost.api.representations.KanBekreftesSomBehandletKvittering;

public class Feil extends ForretningsKvittering {

    private Feiltype feiltype;
    private String detaljer;

    private Feil(KanBekreftesSomBehandletKvittering kanBekreftesSomBehandletKvittering, Kvitteringsinfo kvitteringsinfo, Feiltype feiltype) {
        super(kanBekreftesSomBehandletKvittering, kvitteringsinfo);
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
                "konversasjonsId=" + super.kvitteringsinfo.konversasjonsId +
                ", feiltype=" + feiltype +
                ", detaljer='" + detaljer + '\'' +
                '}';
    }

    public static Builder builder(KanBekreftesSomBehandletKvittering kanBekreftesSomBehandletKvittering, Kvitteringsinfo kvitteringsinfo, Feiltype feiltype) {
        return new Builder(kanBekreftesSomBehandletKvittering, kvitteringsinfo, feiltype);
    }

    public static class Builder {
        private Feil target;
        private boolean built = false;

        public Builder(KanBekreftesSomBehandletKvittering kanBekreftesSomBehandletKvittering, Kvitteringsinfo kvitteringsinfo, Feiltype feiltype) {
            target = new Feil(kanBekreftesSomBehandletKvittering, kvitteringsinfo, feiltype);
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
