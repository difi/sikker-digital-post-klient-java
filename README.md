Sikker Digital Post Javaklient
==============================

Dette er en Java-klient for sending av sikker digital post for det offentlige.
Formålet er å forenkle integrasjonen som må utføres av avsendervirksomheter.
For mer informasjon om sikker digital post se http://begrep.difi.no/SikkerDigitalPost/.

Forutsetninger
--------------

For å starte sending av digital post må følgende være på plass:

* Avsender må være registrert hos Meldingsformidler
* Avsender må være registrert hos postkassene
* Avsender må ha et gyldig virksomhetssertifikat

Tekniske krav:

* Java 1.6 eller nyere
* (Maven for å hente ned sikker-digital-post-java-klient)

Sertifikater
------------

For å bruke klienten må det settes opp en keystore med et gyldig virksomhetssertifikat. Keystoren må inneholde sertifikatkjeden helt opp til rot-CAen for sertifikatutstederen.
En PKCS#12-fil fra en sertifikatutsteder vil normalt inneholde alle de nødvendige sertifikatene.

Bruk Java Keytool for å opprette et keystore fra en PKCS#12-fil (.p12):

```bash
keytool -importkeystore -srckeystore pcks12-fil.p12 -srcstoretype pkcs12 -destkeystore min-keystore.jce -deststoretype jceks
```

Eksempelkode
------------

Det er satt opp et <a href="https://github.com/digipost/sikker-digital-post-test-sender">eksempelprosjekt</a> som viser bruk av hele klienten til å gjennomføre sending av brev og henting av kvitteringer.

Hva ligger i klientbiblioteket
------------------------------

* Bygge meldinger som inneholder EBMS, StandardBusinessDocument, ASIC-E dokumentpakke
* Sende meldinger:
    * Sende digital post
    * Hente kvittering
    * Bekrefte kvittering


Hvordan ta dette i bruk
-----------------------

Artifakten kan lastes ned fra Maven central.

Legg til følgende i POM:

```xml
<dependency>
    <groupId>no.difi.sdp</groupId>
    <artifactId>sikker-digital-post-java-klient</artifactId>
    <version>0.1</version>
</dependency>
```

Tips og triks
-------------

### MPC Id (adskilte køer for en avsender)

Sett en unik MPC Id på Avsender for unngå at det konsumeres kvitteringer på tvers av ulike avsendere med samme organisasjonsnummer.
Dette kan være nyttig i større organisasjoner som har flere avsenderenheter. I tillegg kan det være ekstremt nyttig i utvikling for å unngå at utviklere og testmiljøer går i beina på hverandre.

### Logging av request og respons

Klienten støtter registrering av spring-ws interceptors som kan brukes til logging av request og respons, samt annen feilhåndtering.
Eksempelet under viser hvordan dette kan benyttes til å logge utgående requests til `System.out` ved hjelp av `javax.xml.transform`.

```java
KlientKonfigurasjon klientKonfigurasjon = KlientKonfigurasjon.builder()
    .interceptors(new ClientInterceptor() {
        @Override
        public boolean handleRequest(MessageContext messageContext) throws WebServiceClientException {
            Source payloadSource = messageContext.getRequest().getPayloadSource();
            try {
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                StringWriter writer = new StringWriter();
                transformer.transform(payloadSource, new StreamResult(writer));
                System.out.println(writer.toString());
            } catch (Exception e) {
                System.err.print("Klarte ikke logge request");
                e.printStackTrace();
            }
            return true;
        }

        public boolean handleResponse(MessageContext messageContext) throws WebServiceClientException { return true; }
        public boolean handleFault(MessageContext messageContext) throws WebServiceClientException { return true; }
        public void afterCompletion(MessageContext messageContext, Exception ex) throws WebServiceClientException { }
    })
    .build();
```

Debugging
---------

***Merk: innstillingene under er ikke anbefalt i produksjonsmiljøer.***

Den underliggende http-klienten har støtte for å logge meldingene som sendes over nettverket. Sett `org.apache.http.wire` til `debug` eller lavere for å slå på denne loggingen. 
Alternativt kan logging av requests gjøres ved hjelp av interceptors som beskrevet lengre oppe.

Biblioteket har innebygd støtte for å outputte den genererte ASiC-E Dokumentpakken til disk for debug-formål:

```java
try {
    File tempFile = File.createTempFile("dokumentpakke", "debug");
    CreateASiCE.debug_writeArchiveToDisk(tempFile);
    System.out.println(tempFile);
} catch (IOException e) {
    throw new RuntimeException("Kunne ikke lagre dokumentpakke", e);
}
```


Spørsmål
------------------

Registrer gjerne spørsmål og kommentarer under Issues.
