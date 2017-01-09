---
title: Feilhandtering
identifier: feilhandtering
layout: default
---

Exception-hierarkiet i klienten er plassert under `SikkerDigitalPostException`. Deretter er de grovt kategorisert i `KonfigurasjonException` og `SendException`.
 
`KonfigurasjonException` er typisk feil relatert til konfigurasjon av klienten, som for eksempel manglende støtte for XML-standarder i Java-installasjonen, ugyldig keystore og lignende. 

`SendException` brukes for feil relatert til gjennomføringen av en sending. Disse blir forsøkt markert med om feilen skyldes forhold på klienten eller serveren. 
Dersom en `SendException` skyldes feil på klienten vil det generelt ikke være hensiktsmessig å gjøre en automatisk retry av forsendelsen.
 
### Custom mapping av feil

Det er mulig å registrerte en `ExceptionMapper` for oversetting av feil som oppstår i forbindelse med sending av post. Dette gjøres med `SikkerDigitalPostKlient.setExceptionMapper()`.