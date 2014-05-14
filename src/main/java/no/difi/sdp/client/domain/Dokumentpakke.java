package no.difi.sdp.client.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Dokumentpakke {

    private Dokumentpakke(Dokument hoveddokument) {
        this.hoveddokument = hoveddokument;
    }

    private Dokument hoveddokument;

    private List<Dokument> vedlegg = Collections.emptyList();

    public static Builder builder(Dokument hoveddokument) {
        return new Builder(hoveddokument);
    }

    public static class Builder {

        private final Dokumentpakke target;

        public Builder(Dokument hoveddokument) {
            target = new Dokumentpakke(hoveddokument);
        }

        public Builder vedlegg(List<Dokument> vedlegg) {
            target.vedlegg = new ArrayList<Dokument>(vedlegg);
            return this;
        }

        public Dokumentpakke build() {
            return target;
        }
    }
}