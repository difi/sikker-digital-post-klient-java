---
title: Tips og triks
identifier: tipstriks
layout: default
---

### MPC Id (adskilte køer for en avsender)

Sett en unik MPC Id på Avsender for unngå at det konsumeres kvitteringer på tvers av ulike avsendere med samme organisasjonsnummer.
Dette kan være nyttig i større organisasjoner som har flere avsenderenheter. I tillegg kan det være ekstremt nyttig i utvikling for å unngå at utviklere og testmiljøer går i beina på hverandre.

### Validering av PDF dokumenter som sendes til SDP utskriftstjenesten

Denne klienten støtter også å sende PDF dokumenter til SDP utskriftstjenesten. Dokumenter som skal sendes som fysisk post vil valideres av SDP utskriftstjenesten. [Det finnes et eget Java bibliotek](https://github.com/digipost/printability-validator) som kan benyttes for å validere printbarheten til PDF dokumentet før det faktisk sendes til SDP utskriftstjenesten. Det er fornuftig å alltid gjøre slik validering før request sendes til SDP meldingsformidlertjeneste.
