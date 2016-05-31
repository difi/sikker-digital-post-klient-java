---
title: Ytelse
identifier: ytelse
layout: default
---

For å forbedre ytelsen i klientbiblioteket, kan man med fordel sette følgende system properties på jvm-en.
Ved å gjøre dette, unngår man unødvendig service loading: 

    javax.xml.parsers.DocumentBuilderFactory=com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl
    javax.xml.transform.TransformerFactory=com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl
    com.sun.org.apache.xalan.internal.xsltc.dom.XSLTCDTMManager=com.sun.org.apache.xalan.internal.xsltc.dom.XSLTCDTMManager

For å forbedre mulighetene for parallelitet ytterligere, kan man vurdere å ta i bruk følgende fork av Oracles standard SAAJ implementasjon:

https://github.com/digipost/saaj

Denne forken har noen commits i *release-branch* branchen, som lar en konfigurere størrelsen på SAX parser poolen som brukes av SAAJ: 

    com.sun.xml.messaging.saaj.soap.saxParserPoolSize=75

Merk at bruk av denne forken ikke er støttet.
