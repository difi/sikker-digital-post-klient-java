---
title: Logging av forespørsel og respons
identifier: logging
layout: default
---

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

> Merk: Innstillingene under er ikke anbefalt i produksjonsmiljøer.

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

