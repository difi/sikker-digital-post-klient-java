package no.difi.sdp.client.domain.exceptions;

import org.springframework.ws.soap.SoapMessage;

public class TransportException extends SikkerDigitalPostException {

    private final AntattSkyldig antattSkyldig;

    public TransportException(String message, AntattSkyldig antattSkyldig, Exception e) {
        super(message, e);
        this.antattSkyldig = antattSkyldig;
    }

    public AntattSkyldig getAntattSkyldig() {
        return antattSkyldig;
    }

    public enum AntattSkyldig {
        /**
         * Feilen er trolig forårsaket av en feil i klienten eller klientoppsettet.
         *
         * Å forsøke samme forespørsel igjen vil sannsynligvis ikke gjøre noe med situasjonen.
         */
        KLIENT,

        /**
         * Feilen er trolig forårsaket av en feil i meldingsformidleren.
         *
         * Det kan fungere å prøve forespørselen igjen senere.
         */
        SERVER,

        /**
         * Uvisst om feilen er forårsaket av klienten eller meldingsformidleren.
         */
        UKJENT;

        public static AntattSkyldig fraSoapFault(SoapMessage soapError) {
            if (soapError == null || soapError.getFaultCode() == null) {
                return UKJENT;
            }

            String localPart = soapError.getFaultCode().getLocalPart();
            if ("Receiver".equals(localPart)) {
                return SERVER;
            } else if("Sender".equals(localPart)) {
                return KLIENT;
            } else {
                return UKJENT;
            }
        }
    }

}
