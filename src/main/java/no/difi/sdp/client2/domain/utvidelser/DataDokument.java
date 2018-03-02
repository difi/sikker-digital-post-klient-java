package no.difi.sdp.client2.domain.utvidelser;

import no.difi.sdp.client2.asice.AsicEAttachable;
import no.difi.sdp.client2.domain.exceptions.SendException.AntattSkyldig;
import no.difi.sdp.client2.domain.exceptions.XmlValideringException;
import org.springframework.core.io.Resource;
import org.springframework.oxm.MarshallingFailureException;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.xml.sax.SAXParseException;

import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;

import static no.difi.sdp.client2.domain.utvidelser.Marshalling.getMarshaller;

public abstract class DataDokument implements AsicEAttachable {

    private final String filnavn;
    private final String mimeType;
    private final Jaxb2Marshaller marshaller;

    private byte[] bytes = null;


    DataDokument(String filnavn, String mimeType, Class<?> type, Resource schema) {
        this.filnavn = filnavn;
        this.mimeType = mimeType;
        this.marshaller = getMarshaller(schema, type);
    }

    abstract Object jaxbObject();

    @Override
    public String getFileName() {
        return filnavn;
    }

    @Override
    public byte[] getBytes() {
        if (this.bytes == null) {
            this.bytes = asBytes();
        }
        return this.bytes;
    }

    @Override
    public String getMimeType() {
        return mimeType;
    }

    private byte[] asBytes() {
        ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
        try {
            marshaller.marshal(jaxbObject(), new StreamResult(dataStream));
        } catch (MarshallingFailureException e) {
            if (e.getMostSpecificCause() instanceof SAXParseException) {
                throw new XmlValideringException("Kunne ikke validere generert XML for '" + filnavn + "'. Sjekk at alle påkrevde input er satt og ikke er null",
                        AntattSkyldig.KLIENT, (SAXParseException) e.getMostSpecificCause());
            }
            throw e;
        }
        return dataStream.toByteArray();
    }


}
