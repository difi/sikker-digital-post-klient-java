package no.difi.sdp.client2.asice;

import no.difi.sdp.client2.ObjectMother;
import no.difi.sdp.client2.domain.Dokument;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class CreateASiCETest {
    @Test
    public void get_correct_unzipped_content_bytes_count() {
        Dokument attachment = ObjectMother.forsendelse().getDokumentpakke().getHoveddokument();

        List<Dokument> attachments = Arrays.asList(attachment, attachment, attachment);
        long unzippedContentBytesCount = CreateASiCE.getUnzippedContentBytesCount(new ArrayList<>(attachments));

        assertThat(unzippedContentBytesCount, equalTo((long) attachment.getBytes().length * attachments.size()));
    }
}