package no.difi.sdp.client2.internal;

import no.difi.sdp.client2.ObjectMother;
import no.difi.sdp.client2.domain.Noekkelpar;
import no.difi.sdp.client2.domain.Sertifikat;
import org.bouncycastle.cms.CMSEnvelopedDataParser;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.PrivateKey;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class CreateCMSDocumentTest {

    private CreateCMSDocument sut;
    private PrivateKey privateKey;
    private Sertifikat sertifikat;

    @BeforeEach
    public void set_up() {
        Noekkelpar noekkelpar = ObjectMother.selvsignertNoekkelparUtenTrustStore();
        privateKey = noekkelpar.getVirksomhetssertifikatPrivatnoekkel();
        sertifikat = noekkelpar.getVirksomhetssertifikat();

        sut = new CreateCMSDocument();
    }

    @Test
    public void can_be_decrypted_by_recipient() throws Exception {
        CMSDocument cms = sut.createCMS("message".getBytes(), sertifikat);

        CMSEnvelopedDataParser cmsEnvelopeParser = new CMSEnvelopedDataParser(cms.getBytes());
        JceKeyTransEnvelopedRecipient keyDecoder = new JceKeyTransEnvelopedRecipient(privateKey);

        RecipientInformation recInfo = cmsEnvelopeParser.getRecipientInfos().getRecipients().iterator().next();
        byte[] decryptedContent = recInfo.getContent(keyDecoder);

        assertThat(decryptedContent, equalTo("message".getBytes()));
    }

}