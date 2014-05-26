package no.difi.sdp.client.internal;

import no.difi.sdp.client.asice.CreateASiCE;
import no.difi.sdp.client.domain.Avsender;
import no.difi.sdp.client.domain.Forsendelse;
import no.difi.sdp.client.domain.Sertifikat;
import no.difi.sdp.client.util.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class CreateDokumentpakke {

    private final CreateASiCE createASiCE;
    private final CreateCMSDocument createCMS;

    public CreateDokumentpakke() {
        createASiCE = new CreateASiCE();
        createCMS = new CreateCMSDocument();
    }

    public InputStream createDokumentpakke(Avsender avsender, Forsendelse forsendelse) {
        InputStream asicStream = createASiCE.createStream(avsender, forsendelse);

        byte[] bytes = IOUtils.toByteArrayCloseStream(asicStream);

        Sertifikat mottakerSertifikat = forsendelse.getDigitalPost().getMottaker().getSertifikat();

        CMSDocument cms = createCMS.createCMS(bytes, mottakerSertifikat);
        return new ByteArrayInputStream(cms.getBytes());
    }



}
