package no.difi.sdp.client.internal;

import no.difi.sdp.client.asice.CreateASiCE;
import no.difi.sdp.client.domain.Avsender;
import no.difi.sdp.client.domain.Forsendelse;
import no.difi.sdp.client.domain.Sertifikat;
import no.difi.sdp.client.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class CreateDokumentpakke {

    private final CreateASiCE createASiCE;
    private final CreateCMSDocument createCMS;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public CreateDokumentpakke() {
        createASiCE = new CreateASiCE();
        createCMS = new CreateCMSDocument();
    }

    public InputStream createDokumentpakke(Avsender avsender, Forsendelse forsendelse) {
        log.info("Creating dokumentpakke");
        InputStream asicStream = createASiCE.createStream(avsender, forsendelse);

        byte[] bytes = IOUtils.toByteArrayCloseStream(asicStream);

        Sertifikat mottakerSertifikat = forsendelse.getDigitalPost().getMottaker().getSertifikat();

        log.info("Creating CMS document");
        CMSDocument cms = createCMS.createCMS(bytes, mottakerSertifikat);
        return new ByteArrayInputStream(cms.getBytes());
    }



}
