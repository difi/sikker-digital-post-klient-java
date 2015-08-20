Sikker Digital Post Java klient
==============================

Dette er en Java-klient for sending av sikker digital post for det offentlige.
Formålet er å forenkle integrasjonen som må utføres av avsendervirksomheter.
For mer informasjon om sikker digital post se http://begrep.difi.no/SikkerDigitalPost/.

Klientbiblioteket tilbyr et enkelt grensesnitt for å generere og sende digital post, samt hente og bekrefte kvitteringer.
Det er også mulig å bruke det på et lavere nivå til f.eks. opprettelse av dokumentpakke.

For å starte sending av digital post må:

* Avsender må være registrert hos Meldingsformidler
* Avsender må være registrert hos postkassene
* Avsender må ha et gyldig virksomhetssertifikat

Mottaker av digital post må slås opp i <a href="https://github.com/difi/kontaktregisteret-klient">kontaktregisteret</a>. Dette håndteres ikke av klienten.

Getting started
---------------

### Eksempelkode

Det er satt opp et <a href="https://github.com/difi/sdp-klient-eksempel-java-jetty">eksempelprosjekt</a> som viser bruk av hele klienten til å gjennomføre sending av brev og henting av kvitteringer.

### Tekniske krav

* Java 1.6 eller nyere
* Legge inn JCE Unlimited Strength JAR for å støtte lengre nøkkellengde på plattformen. Se https://www.google.no/search?q=java+cryptography+extension+unlimited+strength. Last ned og legg inn den som er riktig for din Java versjon. Se README i zipen for mer informasjon.
* Maven for å laste ned sikker-digital-post-java-klient

### Legg inn biblioteket som avhengighet

Biblioteket er releaset som en avhengighet til <a href="http://mvnrepository.com/artifact/no.difi.sdp/sikker-digital-post-java-klient">maven central</a>.

### Sertifikater

For å bruke klienten må det settes opp en keystore med et gyldig virksomhetssertifikat. Keystoren må inneholde sertifikatkjeden helt opp til rot-CAen for sertifikatutstederen.
En PKCS#12-fil fra en sertifikatutsteder vil normalt inneholde alle de nødvendige sertifikatene.

Bruk Java Keytool for å opprette et keystore fra en PKCS#12-fil (.p12):

```bash
keytool -importkeystore -srckeystore pcks12-fil.p12 -srcstoretype pkcs12 -destkeystore min-keystore.jce -deststoretype jceks
```

#### Tiltrodde rotsertifikater

I tillegg til avsenders eget sertifikat må det installeres rotsertifikater som avsenderen stoler på til å identifisere meldingsformidler og postkasser.
Oversikt over disse sertifikatene finnes i <a href="http://begrep.difi.no/SikkerDigitalPost/sikkerhet/sertifikathandtering">begrepskatalogen</a>.

Tiltrodde rotsertifikater kan enten installeres avsenders egen keystore eller i <a href="http://docs.oracle.com/cd/E19830-01/819-4712/ablqw/index.html">Java sin truststore</a>.

### Generere og sende digital post

Grensesnittet mot klienten er `SikkerDigitalPostKlient.java`. Alle objektene denne tar inn som input følger et enkelt builder-pattern.
Argumentene som tas inn av `builder`-metoden er obligatoriske, mens det kan settes frivillige felter på builder-objektet som returneres.

I eksempelet under er brevtittel og brevfil obligatorisk, mens mime type er frivillig:

```java
Dokument dokument = Dokument.builder("Svar på søknad", brevfil)
        .mimeType("application/pdf")
        .build();
```
Digital post genereres ved å lage opprette `Forsendelse.java`:

```java
//Opprett Behandlingsansvarlig, DigitalPost og Dokumentpakke med builder pattern som beskrevet over
Forsendelse.digital(behandlingsansvarlig, digitalPost, dokumentpakke)
                .prioritet(Prioritet.NORMAL)
                .spraakkode("NO")
                .build();
```

`KlientKonfigurasjon.java` er en klasse der man kan konfigurere opp en del parametre som proxy, connection timeout, socket timeout, interceptors mm.
Det er en del default verdier satt her allerede. Man kan feks også overstyre hvilken meldingsformidler(adresse) man vil sende mot.

For å utføre sende sendingen kaller man ganske enkelt `send`i `SikkerDigitalPostKlient.java`:

```java
//Opprett Avsender og KlientKonfigurasjon med builder pattern som beskrevet over
SikkerDigitalPostKlient postklient = new SikkerDigitalPostKlient(avsender, klientKonfigurasjon);
postklient.send(forsendelse);
```

Ved henting og bekreftelse av kvittering har man muligheten til å gjøre dette enten i to separate operasjoner eller hente neste kvittering samt bekrefte forrige i én og samme operasjon.
Første eksempel:

```java
KvitteringForespoersel kvitteringForespoersel = KvitteringForespoersel.builder(Prioritet.NORMAL).build();
ForretningsKvittering forretningsKvittering = postklient.hentKvittering(kvitteringForespoersel);

postklient.bekreft(forretningsKvittering);
```

Andre eksempel:
```java
ForretningsKvittering forretningsKvittering = postklient.hentKvitteringOgBekreftForrige(kvitteringForespoersel, forrigeKvittering);
```

Feilhåndtering
--------------

Exception-hierarkiet i klienten er plassert under `SikkerDigitalPostException`. Deretter er de grovt kategorisert i `KonfigurasjonException` og `SendException`.
 
`KonfigurasjonException` er typisk feil relatert til konfigurasjon av klienten, som for eksempel manglende støtte for XML-standarder i Java-installasjonen, ugyldig keystore og så videre. 

`SendException` brukes for feil relatert til gjennomføringen av en sending. Disse blir forsøkt markert med om feilen skyldes forhold på klienten eller serveren. 
Dersom en `SendException` skyldes feil på klienten vil det generelt ikke være hensiktsmessig å gjøre en automatisk retry av forsendelsen.
 
### Custom mapping av feil

Det er mulig å registrerte en `ExceptionMapper` for oversetting av feil som oppstår i forbindelse med sending av post. Dette gjøres med `SikkerDigitalPostKlient.setExceptionMapper()`.


Tips og triks
-------------

### Dependencies

Klientbiblioteket baserer seg på Digipost-biblioteket <a href="https://github.com/digipost/sdp-shared">sdp-shared</a> for sending. Under utvikling vil dette typisk være en SNAPSHOT-avhengighet som må releases i forkant av en ny lansering av klientbiblioteket.

### MPC Id (adskilte køer for en avsender)

Sett en unik MPC Id på Avsender for unngå at det konsumeres kvitteringer på tvers av ulike avsendere med samme organisasjonsnummer.
Dette kan være nyttig i større organisasjoner som har flere avsenderenheter. I tillegg kan det være ekstremt nyttig i utvikling for å unngå at utviklere og testmiljøer går i beina på hverandre.

### Logging av request og respons

Klienten støtter registrering av spring-ws interceptors som kan brukes til logging av request og respons, samt annen feilhåndtering.
Eksempelet under viser hvordan dette kan benyttes til å logge utgående requests til `System.out` ved hjelp av `javax.xml.transform`.

```java
KlientKonfigurasjon klientKonfigurasjon = KlientKonfigurasjon.builder()
    .soapInterceptors(new ClientInterceptor() {
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

### Interceptors

I tillegg til spring-ws interceptors støtter også klienten registrering av HttpRequestInterceptor og HttpResponseInterceptor for direkte tilgang til underliggende http request og response:

```java
KlientKonfigurasjon.builder()
    .httpRequestInterceptors(new HttpRequestInterceptor() {
        @Override
        public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
            System.out.println("Utgående request!");
        }
    })
    .httpResponseInterceptors(new HttpResponseInterceptor() {
        @Override
        public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
            System.out.println("Innkommende request!");
        }
    })
    .build();
```

### Debugging

***Merk: innstillingene under er ikke anbefalt i produksjonsmiljøer.***

Den underliggende http-klienten har støtte for å logge meldingene som sendes over nettverket. Sett `org.apache.http.wire` til `debug` eller lavere for å slå på denne loggingen. 
Alternativt kan logging av requests gjøres ved hjelp av interceptors som beskrevet over.

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

### Validering av PDF dokumenter som sendes til SDP utskriftstjenesten

Denne klienten støtter også å sende PDF dokumenter til SDP utskriftstjenesten. Dokumenter som skal sendes som fysisk post vil valideres av SDP utskriftstjenesten. [Det finnes et eget Java bibliotek](https://github.com/digipost/printability-validator) som kan benyttes for å validere printbarheten til PDF dokumentet før det faktisk sendes til SDP utskriftstjenesten. Det er fornuftig å alltid gjøre slik validering før request sendes til SDP meldingsformidlertjeneste.
