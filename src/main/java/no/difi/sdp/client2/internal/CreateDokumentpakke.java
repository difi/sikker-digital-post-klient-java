package no.difi.sdp.client2.internal;

import no.difi.sdp.client2.asice.ArchivedASiCE;
import no.difi.sdp.client2.asice.CreateASiCE;
import no.difi.sdp.client2.domain.Forsendelse;
import no.difi.sdp.client2.domain.Sertifikat;
import no.difi.sdp.client2.domain.TekniskAvsender;
import no.digipost.api.representations.Dokumentpakke;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateDokumentpakke {

    private final CreateASiCE createASiCE;
    private final CreateCMSDocument createCMS;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public CreateDokumentpakke() {
        createASiCE = new CreateASiCE();
        createCMS = new CreateCMSDocument();
    }

    public Dokumentpakke createDokumentpakke(TekniskAvsender tekniskAvsender, Forsendelse forsendelse) {
        log.info("Creating dokumentpakke");
        ArchivedASiCE archivedASiCE = createASiCE.createAsice(tekniskAvsender, forsendelse);

        Sertifikat mottakerSertifikat = forsendelse.getTekniskMottaker().sertifikat;

        log.info("Creating CMS document");
        CMSDocument cms = createCMS.createCMS(archivedASiCE.getBytes(), mottakerSertifikat);
        return new Dokumentpakke(cms.getBytes());
    }

}
