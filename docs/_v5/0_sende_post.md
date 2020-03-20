---
title: Sende post
identifier: sendepost
layout: default
---

For å sende post så må du først lage en `DigitalPost` eller en `FysiskPost`, så opprette en `Forsendelse` og legge ved posten. Deretter sendes denne gjennom en `SikkerDigitalPostKlient`. Når brevet er sendt så kan du spørre om status på en meldingskø. Hvis du mottar en kvittering så kan du sjekke innholdet og så bekrefte mottatt kvittering.

### Opprette digital post

```java
Sertifikat mottakerSertifikat = null;   //Fås fra Oppslagstjenesten
String orgnrPostkasse = null;           //Fås fra Oppslagstjenesten
String postkasseadresse = null;         //Fås fra Oppslagstjenesten

Mottaker mottaker = Mottaker
        .builder(
                "99999999999",
                postkasseadresse,
                mottakerSertifikat,
                Organisasjonsnummer.of(orgnrPostkasse))
        .build();


SmsVarsel smsVarsel = SmsVarsel.builder("4799999999",
        "Du har mottatt brev i din digitale postkasse")
        .build();

EpostVarsel epostVarsel = EpostVarsel.builder("epost@example.com",
        "Du har mottatt brev i din digitale postkasse")
        .varselEtterDager(asList(1, 4, 10))
        .build();

DigitalPost digitalPost = DigitalPost
        .builder(mottaker, "Ikke-sensitiv tittel")
        .virkningsdato(new Date())
        .aapningskvittering(false)
        .sikkerhetsnivaa(Sikkerhetsnivaa.NIVAA_3)
        .epostVarsel(epostVarsel)
        .smsVarsel(smsVarsel)
        .build();
```

### Opprette fysisk post

```java
Sertifikat utskriftsleverandørSertifikat = null;    //Printsertifikat fra Oppslagstjenesten
TekniskMottaker utskriftsleverandør =
        new TekniskMottaker(Organisasjonsnummer.of("99999999"), utskriftsleverandørSertifikat);

FysiskPost fysiskPost = FysiskPost.builder()
        .adresse(
                KonvoluttAdresse.build("Ola Nordmann")
                        .iNorge("Fjellheimen 22", "", "", "0001", "Oslo")
                        .build())
        .retur(
                Returhaandtering.DIREKTE_RETUR.MAKULERING_MED_MELDING,
                KonvoluttAdresse.build("Returkongen")
                        .iNorge("Returveien 3", "", "", "0002", "Oslo")
                        .build())
        .sendesMed(Posttype.A_PRIORITERT)
        .utskrift(Utskriftsfarge.FARGE, utskriftsleverandør)
        .build();
```

### Opprette selve forsendelsen

```java
DigitalPost digitalPost = null; //Som initiert tidligere

Dokument hovedDokument = Dokument
        .builder("Sensitiv brevtittel", new File("/sti/til/dokument"))
        .mimeType("application/pdf")
        .build();

Dokumentpakke dokumentpakke = Dokumentpakke.builder(hovedDokument)
        .vedlegg(new ArrayList<Dokument>())
        .build();

AvsenderOrganisasjonsnummer avsenderOrgnr =
        AktoerOrganisasjonsnummer.of("999999999").forfremTilAvsender();

Avsender avsender = Avsender
        .builder(avsenderOrgnr)
        .build();

Forsendelse forsendelse = Forsendelse
        .digital(avsender, digitalPost, dokumentpakke)
        .konversasjonsId(UUID.randomUUID().toString())
        .prioritet(Prioritet.NORMAL)
        .mpcId("KøId")
        .spraakkode("NO")
        .build();
```

> Sett en unik `Forsendelse.mpcId` for å unngå at det konsumeres kvitteringer på tvers av ulike avsendere med samme organisasjonsnummer. Dette er nyttig i større organisasjoner som har flere avsenderenheter. I tillegg kan det være veldig nyttig i utvikling for å unngå at utviklere og testmiljøer går i beina på hverandre.

### Utvidelser
Difi har egne dokumenttyper, eller utvidelser, som kan sendes som metadata til hoveddokumenter. Disse utvidelsene er strukturerte xml-dokumenter
med egne mime-typer. Disse utvidelsene benyttes av postkasseleverandørene til å gi en øket brukeropplevelse for innbyggere.
Les mer om utvidelser på [https://difi.github.io/felleslosninger/](https://difi.github.io/felleslosninger/sdp_utvidelser_index.html)

Utvidelsene ligger som generert kode i `sdp-shared`, som er en avhengighet av `sikker-digital-post-klient-java`. Du kan selv lage kode
for å generere xml fra instanser av disse typene med JAXB, eller du kan lage xml på andre måter.

```java
SDPLenke lenke = new SDPLenke();
lenke.setUrl("http://example.com");

StringResult result = new StringResult();
JAXBContext.newInstance(SDPLenke.class).createMarshaller().marshal(lenke, result);

MetadataDokument innkalling = MetadataDokument.builder(
        "lenke.xml", 
        "application/vnd.difi.dpi.lenke+xml", 
        result.toString().getBytes()
).build();

Dokumentpakke dokumentpakke = Dokumentpakke.builder(hovedDokument)
    .metadataDocument(innkalling)
    .build();
```

### Opprette klient og sende post

```java
Forsendelse forsendelse = null;         //Som initiert tidligere
KeyStore virksomhetssertifikat = null;  //Last inn sertifikat her.

KlientKonfigurasjon klientKonfigurasjon = KlientKonfigurasjon
        .builder(Miljo.FUNKSJONELT_TESTMILJO)
        .connectionTimeout(20, TimeUnit.SECONDS)
        .build();

DatabehandlerOrganisasjonsnummer databehandlerOrgnr =
        AktoerOrganisasjonsnummer.of("555555555").forfremTilDatabehandler();

Databehandler databehandler = Databehandler
        .builder(
                databehandlerOrgnr,
                Noekkelpar.fraKeyStoreUtenTrustStore(
                        virksomhetssertifikat,
                        "sertifikatAlias",
                        "sertifikatPassord"))
        .build();

SikkerDigitalPostKlient sikkerDigitalPostKlient = new SikkerDigitalPostKlient(databehandler, klientKonfigurasjon);

try {
    sikkerDigitalPostKlient.send(forsendelse);
} catch (SendException sendException) {
    SendException.AntattSkyldig antattSkyldig = sendException.getAntattSkyldig();
    String message = sendException.getMessage();
}

```

> Mer informasjon om hvordan du oppretter et `Noekkelpar` finner du [her](#noekkelpar).

### Hent kvittering og bekreft

```java
SikkerDigitalPostKlient sikkerDigitalPostKlient = null;     //Som initiert tidligere

KvitteringForespoersel kvitteringForespoersel = KvitteringForespoersel.builder(Prioritet.NORMAL).mpcId("KøId").build();
ForretningsKvittering forretningsKvittering = sikkerDigitalPostKlient.hentKvittering(kvitteringForespoersel);

if (forretningsKvittering instanceof LeveringsKvittering) {
    //Forsendelse er levert til digital postkasse
} else if (forretningsKvittering instanceof AapningsKvittering) {
    //Forsendelse ble åpnet av mottaker
} else if (forretningsKvittering instanceof MottaksKvittering) {
    //Kvittering på sending av fysisk post
} else if (forretningsKvittering instanceof ReturpostKvittering) {
    //Forsendelse er blitt sendt i retur
} else if (forretningsKvittering instanceof Feil) {
    //Feil skjedde under sending
}

sikkerDigitalPostKlient.bekreft(forretningsKvittering);
```

> Husk at det ikke er mulig å hente nye kvitteringer før du har bekreftet mottak av nåværende.

### Hent antall fakturerbare bytes

Hvis det er ønskelig å ha kontroll på hvor mange bytes det blir fakturert for i en forsendelse, så kan dette hentes ut fra
resultatet av en sendoperasjon:

```java
SikkerDigitalPostKlient sikkerDigitalPostKlient = null;     //Som initiert tidligere
Forsendelse forsendelse = null;                             //Som initiert tidligere

SendResultat sendResultat = sikkerDigitalPostKlient.send(forsendelse);
long antallFakturerbareBytes = sendResultat.getFakturerbareBytes();

```

> Husk at antall bytes det faktureres for ikke er synonymt med størrelsen på pakken som sendes. Faktureringsstørrelsen er beregnet fra opprinnelg størrelse på dokumentene og metainformasjonen gitt av manifest- og signaturfil.
