package no.difi.sdp.client.domain.exceptions;

import org.xml.sax.SAXParseException;

import static java.util.Arrays.asList;

public class XmlValidationException extends SendException {
    public XmlValidationException(String message, SAXParseException[] errors, AntattSkyldig antattSkyldig) {
        super(message + "\n" + asList(errors).toString(), antattSkyldig, errors[0]);
    }

}
