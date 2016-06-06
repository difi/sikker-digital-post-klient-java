package no.difi.sdp.client2.domain.kvittering;

import no.digipost.api.representations.KanBekreftesSomBehandletKvittering;

public class VarslingFeiletKvittering extends ForretningsKvittering {

    private Varslingskanal varslingskanal;
    private String beskrivelse;

    private VarslingFeiletKvittering(KanBekreftesSomBehandletKvittering kanBekreftesSomBehandletKvittering, Kvitteringsinfo kvitteringsinfo, Varslingskanal varslingskanal) {
        super(kanBekreftesSomBehandletKvittering, kvitteringsinfo);
        this.varslingskanal = varslingskanal;
    }

    public Varslingskanal getVarslingskanal() {
        return varslingskanal;
    }

    public String getBeskrivelse() {
        return beskrivelse;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "konversasjonsId=" + super.kvitteringsinfo.konversasjonsId +
                ", varslingskanal=" + varslingskanal +
                ", beskrivelse='" + beskrivelse + '\'' +
                '}';
    }

    public static Builder builder(KanBekreftesSomBehandletKvittering kanBekreftesSomBehandletKvittering, Kvitteringsinfo kvitteringsinfo, Varslingskanal varslingskanal) {
        return new Builder(kanBekreftesSomBehandletKvittering, kvitteringsinfo, varslingskanal );
    }

    public static class Builder {
        private VarslingFeiletKvittering target;
        private boolean built = false;

        public Builder(KanBekreftesSomBehandletKvittering kanBekreftesSomBehandletKvittering, Kvitteringsinfo kvitteringsinfo, Varslingskanal varslingskanal) {
            target = new VarslingFeiletKvittering(kanBekreftesSomBehandletKvittering, kvitteringsinfo, varslingskanal);
        }

        public Builder beskrivelse(String beskrivelse) {
            target.beskrivelse = beskrivelse;
            return this;
        }

        public VarslingFeiletKvittering build() {
            if (built) throw new IllegalStateException("Can't build twice");
            built = true;
            return target;
        }
    }

    public enum Varslingskanal {
        SMS,
        EPOST
    }
}
