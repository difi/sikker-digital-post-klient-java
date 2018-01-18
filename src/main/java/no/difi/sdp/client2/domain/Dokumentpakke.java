package no.difi.sdp.client2.domain;

import no.difi.sdp.client2.domain.utvidelser.DataDokument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

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

    public Stream<Dokument> alleDokumenter() {
        return Stream.concat(Stream.of(this.hoveddokument), this.vedlegg.stream());
    }

    public Stream<DataDokument> alleDataDokumenter() {
        return alleDokumenter().map(Dokument::getDataDokument).flatMap(d -> d.map(Stream::of).orElseGet(Stream::empty));
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