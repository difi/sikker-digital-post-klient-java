package no.difi.sdp.client.internal;

import no.difi.sdp.client.ObjectMother;
import no.difi.sdp.client.domain.Noekkelpar;
import no.difi.sdp.client.domain.Sertifikat;
import org.bouncycastle.cms.CMSEnvelopedDataParser;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import org.junit.Before;
import org.junit.Test;

import java.security.PrivateKey;

import static org.fest.assertions.api.Assertions.assertThat;

public class CreateCryptographicMessageSyntaxTest {

    private CreateCryptographicMessageSyntax sut;
    private PrivateKey privateKey;
    private Sertifikat sertifikat;

    @Before
    public void setUp() {
        Noekkelpar noekkelpar = ObjectMother.noekkelpar();
        privateKey = noekkelpar.getPrivateKey();
        sertifikat = noekkelpar.getSertifikat();
    }

    @Test
    public void test_can_be_decrypted_by_recipient() throws Exception {
        sut = new CreateCryptographicMessageSyntax();
        CMSDocument cms = sut.createCMS("message".getBytes(), sertifikat);

        CMSEnvelopedDataParser cmsEnvelopeParser = new CMSEnvelopedDataParser(cms.getBytes());
        JceKeyTransEnvelopedRecipient keyDecoder = new JceKeyTransEnvelopedRecipient(privateKey);

        RecipientInformation recInfo = (RecipientInformation) cmsEnvelopeParser.getRecipientInfos().getRecipients().iterator().next();
        byte[] decryptedContent = recInfo.getContent(keyDecoder);

        assertThat(decryptedContent).isEqualTo("message".getBytes());
    }

}