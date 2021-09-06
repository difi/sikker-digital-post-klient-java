package no.difi.sdp.client2.domain.kvittering;

import no.digipost.api.representations.KanBekreftesSomBehandletKvittering;

public class VarslingFeiletKvittering extends ForretningsKvittering {

    private final Varslingskanal varslingskanal;
    private String beskrivelse;

    private VarslingFeiletKvittering(KanBekreftesSomBehandletKvittering kanBekreftesSomBehandletKvittering, KvitteringsInfo kvitteringsInfo, Varslingskanal varslingskanal) {
        super(kanBekreftesSomBehandletKvittering, kvitteringsInfo);
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
                "konversasjonsId=" + getKonversasjonsId() +
                ", varslingskanal=" + varslingskanal +
                ", beskrivelse='" + beskrivelse + '\'' +
                '}';
    }

    public static Builder builder(KanBekreftesSomBehandletKvittering kanBekreftesSomBehandletKvittering, KvitteringsInfo kvitteringsInfo, Varslingskanal varslingskanal) {
        return new Builder(kanBekreftesSomBehandletKvittering, kvitteringsInfo, varslingskanal );
    }

    public static class Builder {
        private VarslingFeiletKvittering target;
        private boolean built = false;

        public Builder(KanBekreftesSomBehandletKvittering kanBekreftesSomBehandletKvittering, KvitteringsInfo kvitteringsInfo, Varslingskanal varslingskanal) {
            target = new VarslingFeiletKvittering(kanBekreftesSomBehandletKvittering, kvitteringsInfo, varslingskanal);
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
