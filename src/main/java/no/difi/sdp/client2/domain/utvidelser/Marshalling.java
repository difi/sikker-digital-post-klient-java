package no.difi.sdp.client2.domain.utvidelser;

import no.difi.sdp.client2.domain.exceptions.KonfigurasjonException;
import org.springframework.core.io.Resource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

final class Marshalling {

    private static ConcurrentMap<Resource, Jaxb2Marshaller> marshallers = new ConcurrentHashMap<>();

    static Jaxb2Marshaller getMarshaller(Resource schema, Class<?> type) {
        return marshallers.computeIfAbsent(schema, schemaResource -> {
            Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
            marshaller.setPackagesToScan(type.getPackage().getName());
            marshaller.setSchema(schemaResource);
            try {
                marshaller.afterPropertiesSet();
            } catch (Exception e) {
                throw new KonfigurasjonException("Kunne ikke sette opp Jaxb marshaller", e);
            }
            return marshaller;
        });
    }

}
