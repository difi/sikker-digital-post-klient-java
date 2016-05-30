package no.difi.sdp.client2.internal;

import no.difi.sdp.client2.ObjectMother;
import no.difi.sdp.client2.domain.Noekkelpar;
import no.difi.sdp.client2.domain.Sertifikat;
import org.bouncycastle.cms.CMSEnvelopedDataParser;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import org.junit.Before;
import org.junit.Test;

import java.security.PrivateKey;

import static org.fest.assertions.api.Assertions.assertThat;

public class CreateCMSDocumentTest {

    private CreateCMSDocument sut;
    private PrivateKey privateKey;
    private Sertifikat sertifikat;

    @Before
    public void setUp() {
        Noekkelpar noekkelpar = ObjectMother.noekkelpar();
        privateKey = noekkelpar.getVirksomhetssertifikatPrivatnøkkel();
        sertifikat = noekkelpar.getVirksomhetssertifikat();

        sut = new CreateCMSDocument();
    }

    @Test
    public void test_can_be_decrypted_by_recipient() throws Exception {
        CMSDocument cms = sut.createCMS("message".getBytes(), sertifikat);

        CMSEnvelopedDataParser cmsEnvelopeParser = new CMSEnvelopedDataParser(cms.getBytes());
        JceKeyTransEnvelopedRecipient keyDecoder = new JceKeyTransEnvelopedRecipient(privateKey);

        RecipientInformation recInfo = (RecipientInformation) cmsEnvelopeParser.getRecipientInfos().getRecipients().iterator().next();
        byte[] decryptedContent = recInfo.getContent(keyDecoder);

        assertThat(decryptedContent).isEqualTo("message".getBytes());
    }

}