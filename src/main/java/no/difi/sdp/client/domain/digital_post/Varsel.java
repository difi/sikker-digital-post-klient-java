package no.difi.sdp.client.domain.digital_post;

import java.util.List;

import static java.util.Arrays.asList;

public abstract class Varsel {

    protected Varsel(String tekst) {
        this.tekst = tekst;
    }

    protected String tekst;
    protected List<Integer> dagerEtter = asList(0);

    public String getTekst() {
        return tekst;
    }

    public List<Integer> getDagerEtter() {
        return dagerEtter;
    }
}
