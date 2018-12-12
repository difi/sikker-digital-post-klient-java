Sikker Digital Post Klient Java
==============================

[![Build Status](https://travis-ci.org/difi/sikker-digital-post-klient-java.svg?branch=master)](https://travis-ci.org/difi/sikker-digital-post-klient-java) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/no.difi.sdp/sikker-digital-post-klient-java/badge.svg)](https://maven-badges.herokuapp.com/maven-central/no.difi.sdp/sikker-digital-post-klient-java)

[DOKUMENTASJON FINNER DU HER](https://difi.github.io/sikker-digital-post-klient-java/)

## Om klienten
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


