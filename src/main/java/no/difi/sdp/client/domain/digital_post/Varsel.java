package no.difi.sdp.client.domain.digital_post;

import java.util.List;

import static java.util.Arrays.asList;

public abstract class Varsel {

    protected Varsel(String tekst) {
        this.tekst = tekst;
    }

    protected String tekst;
    protected String spraakkode = "NO";
    protected List<Integer> dagerEtter = asList(0);
}
