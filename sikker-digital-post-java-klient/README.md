sikker-digital-post-java-klient
===============================

Dette er en Java klient for sending av sikker digital post for det offentlige.
Formålet er å forenkle integrasjonen som må utføres av avsendervirksomheter.
For mer informasjon om sikker digital post se http://begrep.difi.no/SikkerDigitalPost/.

Forutsetninger
--------------

For å starte sending av digital post må følgende være på plass:

* Avsendere må være registrert hos Meldingsformidler
* Avsendere må være registrert hos postkassene
* Avsendere må ha et gyldig virksomhetssertifikat

Tekniske krav:

* Java 1.6 eller nyere
* (Maven for å hente ned sikker-digital-post-klient)

Hva ligger i klientbiblioteket
------------------------------

* Bygge meldinger som inneholder EBMS, StandardBusinessDocument, ASIC-E dokumentpakke
* Sende meldinger:
    * Sende digital post
    * Hente kvittering
    * Bekrefte kvittering

Hva ligger _ikke_ i klientbiblioteket
-------------------------------------

* Oppslag mot Oppslagstjenesten for å hente ut informasjon om mottakeren
* .NET versjon
* Validering utover mandatory felter, feks gyldig format osv


Hvordan ta dette i bruk
-----------------------

Når vi får releaset til Maven Central kan artifakten lastes ned derfra.
Vi sier ifra når dette er ute og oppdaterer dokumentasjonen.


Spørsmål
------------------

Registrer gjerne spørsmål og kommentarer under Issues.
