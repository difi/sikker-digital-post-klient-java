---
title: Sende post
identifier: sendepost
layout: default
---

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