package no.difi.sdp.client;

import no.difi.sdp.client.domain.*;
import org.junit.Test;
import sun.security.x509.X509CertImpl;

import java.io.ByteArrayInputStream;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Date;

import static java.util.Arrays.asList;

public class SikkerDigitalPostKlientTest {

    @Test
    public void test_build_forsendelse() {
        PrivateKey privatnoekkel = null;
        Avsender avsender = Avsender.builder("984661185", new X509CertImpl(), privatnoekkel).build();
        SikkerDigitalPostKlient postklient = new SikkerDigitalPostKlient(avsender, new KlientKonfigurasjon());

        Varsel epostVarsel = Varsel.builder("Du har mottatt brev i din digitale postkasse")
                        .varselEtterDager(asList(1, 4, 10)).build();

        DigitalpostInfo digitalpostInfo = DigitalpostInfo.builder("Ikke-sensitiv tittel for forsendelsen")
                .virkningsdato(new Date())
                .aapningskvittering(false)
                .sikkerhetsnivaa(Sikkerhetsnivaa.NIVAA_3)
                .epostVarsel(epostVarsel)
                .build();

        Dokument hovedDokument = Dokument.builder("Sensitiv brevtittel", "faktura.pdf", new ByteArrayInputStream("hei".getBytes()))
                .mimeType("application/pdf")
                .build();

        Dokumentpakke dokumentpakke = Dokumentpakke.builder(hovedDokument)
                .vedlegg(new ArrayList<Dokument>())
                .build();

        Mottaker mottaker = Mottaker.builder("01129955131", "postkasseadresse", new X509CertImpl(), "984661185")
                .epostadresse("example@email.org")
                .mobilnummer("+4799999999")
                .build();

        Forsendelse forsendelse = Forsendelse.builder(digitalpostInfo, dokumentpakke, mottaker)
                .konversasjonsId("konversasjonsId")
                .prioritet(Prioritet.NORMAL)
                .build();

        postklient.send(forsendelse);


        KvitteringForespoersel kvitteringForespoersel = KvitteringForespoersel.builder(Prioritet.NORMAL).build();
        postklient.hentKvittering(kvitteringForespoersel);
    }

}
