package no.difi.sdp.client2.domain.digital_post;

import java.util.List;

import static java.util.Arrays.asList;

public abstract class Varsel {

    protected Varsel(String varslingsTekst) {
        this.varslingsTekst = varslingsTekst;
    }

    protected String varslingsTekst;
    protected List<Integer> dagerEtter = asList(0);

    public String getVarslingsTekst() {
        return varslingsTekst;
    }

    public List<Integer> getDagerEtter() {
        return dagerEtter;
    }
}
