package no.difi.sdp.client2.domain;

import java.security.KeyStore;

public class NoValidationNoekkelpar extends Noekkelpar {

    public NoValidationNoekkelpar(KeyStore keyStore, String selvsignertVirksomhetssertifikatAlias, String selvsignertVirksomhetssertifikatPassord) {
        super(keyStore, selvsignertVirksomhetssertifikatAlias, selvsignertVirksomhetssertifikatPassord, false);
    }

    public NoValidationNoekkelpar(KeyStore keyStore, KeyStore trustStore, String selvsignertVirksomhetssertifikatAlias, String selvsignertVirksomhetssertifikatPassord) {
        super(keyStore, trustStore, selvsignertVirksomhetssertifikatAlias, selvsignertVirksomhetssertifikatPassord, false);
    }
}
