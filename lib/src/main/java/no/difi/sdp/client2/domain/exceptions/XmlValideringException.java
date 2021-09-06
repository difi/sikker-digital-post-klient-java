package no.difi.sdp.client2.domain.exceptions;

import org.xml.sax.SAXParseException;

import static java.util.Arrays.asList;

public class XmlValideringException extends SendException {
    public XmlValideringException(String message, SAXParseException[] errors, AntattSkyldig antattSkyldig) {
        super(message + "\n" + asList(errors).toString(), antattSkyldig, errors[0]);
    }

    public XmlValideringException(String message, AntattSkyldig antattSkyldig, Exception e) {
        super(message, antattSkyldig, e);
    }
}
