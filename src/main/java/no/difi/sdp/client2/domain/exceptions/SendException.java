package no.difi.sdp.client2.domain.exceptions;

import javax.xml.namespace.QName;

/**
 * Felles superklasse for alle Exceptions som oppstår under sending/mottak av forespørsler mot meldingsformidler.
 */
public class SendException extends SikkerDigitalPostException {

    private final AntattSkyldig antattSkyldig;

    public SendException(String message, AntattSkyldig antattSkyldig, Exception e) {
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

        public static AntattSkyldig fraSoapFaultCode(QName soapFaultCode) {
            if (soapFaultCode == null) {
                return UKJENT;
            }

            String localPart = soapFaultCode.getLocalPart();
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
