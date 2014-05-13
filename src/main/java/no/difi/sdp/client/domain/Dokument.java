package no.difi.sdp.client.domain;

import java.io.InputStream;

public class Dokument {

    /**
     * Tittel som vises til brukeren gitt riktig sikkerhetsnivå.
     */
    private String tittel;

    /**
     * MIME-type for dokumentet. For liste over tillatte MIME-typer, se <a href="http://begrep.difi.no">http://begrep.difi.no</a> }
     */
    private String mimeType;

    /**
     * Filnavnet til dokumentet.
     */
    private String href;
    /**
     * Dokumentet som en strøm
     */
    private InputStream dokument;

}
