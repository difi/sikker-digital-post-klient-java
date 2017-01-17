---
title: Nøkkelpar
identifier: noekkelpar
layout: default
---

For å kunne sende brev så må `Databehandler` initieres med et `Noekkelpar` som innehar virksomhetssertifikat i tillegg til sertifikater som er nødvendig for å validere responsen, såkalte trust-sertifikater. Det er to måter å gjøre det på, og den ene er hakket lettere enn den andre. 

### Opprett nøkkelpar med virksomhetssertifikat
Ved å bruke `Noekkelpar.fraKeyStoreUtenTrustStore(KeyStore, String, String)` så trenger du bare å sende inn en `KeyStore` som er initert med virksomhetssertifikatet til organisasjonen. Innebygd trust store blir da brukt for å validere responsen.

```
KeyStore keyStore;
String virksomhetssertifikatsti = "/sti/til/virksomhetssertifikat.p12";
String virksomhetssertifikatpassord = "virksomhetssertifikat_passord";
try {
    keyStore = KeyStore.getInstance("PKCS12");
    keyStore.load(
            new FileInputStream(virksomhetssertifikatsti),
            virksomhetssertifikatpassord.toCharArray()
    );
} catch (Exception e) {
    throw new RuntimeException(
            MessageFormat.format(
                    "Fant ikke virksomhetssertifikat på sti {0}.",
                    virksomhetssertifikatsti), e
    );
}
```

### Opprett nøkkelpar med virksomhetssertifikat og trust store

Hvis det er et ønske om å bygge sertifikatlageret for trust selv så kan det gjøres ved enten å bruke

* `Noekkelpar.fraKeyStore(KeyStore, String, String)`, hvor `KeyStore` inneholder virksomhetssertifikat og trust-sertifikater eller 
* `Noekkelpar.fraKeyStoreOgTrustStore(KeyStore, KeyStore, String, String)`, hvor virksomhetssertifikat ikke ligger sammen med trust-sertifikater. Sertifikatene kan lastes ned fra [begrep.difi.no](http://begrep.difi.no/SikkerDigitalPost/1.2.3/sikkerhet/sertifikathandtering). Følgende eksempel viser hvordan du kan bygge en trust store.

> Merk at oppretting av en trust store er kun for avsendere som ønsker full kontroll på hvilke sertifikater som man stoler på. Det er ellers ingen grunn til å gjøre det. 


```
KeyStore trustStore;

try {
    trustStore = KeyStore.getInstance("JCEKS");
    trustStore.load(null, "".toCharArray());
} catch (Exception e) {
    throw new SertifikatException(
            MessageFormat.format(
                    "Oppretting av tom keystore feilet. Grunnen er {0}.",
                    e.toString()
            )
    );
}

List<X509Certificate> certificates = null; //Sertifikater lastet fra disk

for(X509Certificate cert : certificates){
    String uniqueCertificateAlias = cert.getSerialNumber().toString() + Math.random();

    try {
        trustStore.setCertificateEntry(uniqueCertificateAlias, cert);
    } catch (KeyStoreException e) {
        throw new RuntimeException(
                MessageFormat.format(
                        "Klarte ikke å legge til sertifikat til trust store. Grunnen er {0}",
                        e.toString()
                )
        );

    }
}
```