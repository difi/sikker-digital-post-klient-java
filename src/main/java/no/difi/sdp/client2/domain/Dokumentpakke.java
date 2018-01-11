package no.difi.sdp.client2.domain;

import no.difi.sdp.client2.domain.utvidelser.DataDokument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;

public class Dokumentpakke {

    private Dokument hoveddokument;
    private List<Dokument> vedlegg = Collections.emptyList();

    private Dokumentpakke(Dokument hoveddokument) {
        this.hoveddokument = hoveddokument;
    }

    public Dokument getHoveddokument() {
        return hoveddokument;
    }

    public List<Dokument> getVedlegg() {
        return vedlegg;
    }

    public List<DataDokument> getDataDokumenter() {
        List<DataDokument> dataDokumenter = new ArrayList<>();
        getHoveddokument().getDataDokument().ifPresent(dataDokumenter::add);
        getVedlegg().stream().map(Dokument::getDataDokument).filter(Optional::isPresent).map(Optional::get).forEach(dataDokumenter::add);
        return dataDokumenter;
    }

    public static Builder builder(Dokument hoveddokument) {
        return new Builder(hoveddokument);
    }

    public static class Builder {

        private final Dokumentpakke target;
        private boolean built = false;

        private Builder(Dokument hoveddokument) {
            target = new Dokumentpakke(hoveddokument);
        }

        public Builder vedlegg(List<Dokument> vedlegg) {
            target.vedlegg = new ArrayList<>(vedlegg);
            return this;
        }

        public Builder vedlegg(Dokument... vedlegg) {
            return this.vedlegg(asList(vedlegg));
        }

        public Dokumentpakke build() {
            if (built) throw new IllegalStateException("Can't build twice");
            built = true;
            return target;
        }
    }
}