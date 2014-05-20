package no.difi.sdp.client.asice;

import no.difi.begrep.sdp.schema_v10.SDPManifest;
import org.etsi.uri._01903.v1_3.QualifyingProperties;
import org.etsi.uri._2918.v1_2.XAdESSignatures;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.w3.xmldsig.X509Data;

public class Jaxb {

    private static final Jaxb2Marshaller marshaller;

    static {
        marshaller = new Jaxb2Marshaller();
        Jaxb2Marshaller marshaller = Jaxb.marshaller;
        marshaller.setClassesToBeBound(SDPManifest.class, XAdESSignatures.class, X509Data.class, QualifyingProperties.class);
    }

    public static Marshaller marshaller() { return marshaller; }

    public static Unmarshaller unmarshaller() { return marshaller; }

}
