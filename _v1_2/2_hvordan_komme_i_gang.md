---
title: Hvordan komme i gang
identifier: hvordankommeigang
layout: default
isHome: false
---

### Eksempelkode

Det er satt opp et <a href="https://github.com/difi/sdp-klient-eksempel-java-jetty">eksempelprosjekt</a> som viser bruk av hele klienten til å gjennomføre sending av brev og henting av kvitteringer.

### Tekniske krav

* Java 1.6 eller nyere
* Legge inn JCE Unlimited Strength JAR for å støtte lengre nøkkellengde på plattformen. Se https://www.google.no/search?q=java+cryptography+extension+unlimited+strength. Last ned og legg inn den som er riktig for din Java versjon. Se README i zipen for mer informasjon.
* Maven for å laste ned sikker-digital-post-java-klient

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

### Dependencies

Klientbiblioteket baserer seg på Digipost-biblioteket <a href="https://github.com/digipost/sdp-shared">sdp-shared</a> for sending. Under utvikling vil dette typisk være en SNAPSHOT-avhengighet som må releases i forkant av en ny lansering av klientbiblioteket.