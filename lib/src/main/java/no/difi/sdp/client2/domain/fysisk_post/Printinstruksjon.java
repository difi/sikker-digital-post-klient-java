package no.difi.sdp.client2.domain.fysisk_post;

public class Printinstruksjon {
    private final String navn;
    private final String verdi;

    public Printinstruksjon(String navn, String verdi) {
        this.navn = navn;
        this.verdi = verdi;
    }

    public String getNavn() {
        return navn;
    }

    public String getVerdi() {
        return verdi;
    }
}
