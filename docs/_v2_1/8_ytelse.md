---
title: Ytelse
identifier: ytelse
layout: default
---

For å forbedre ytelsen i klientbiblioteket, kan man med fordel sette følgende system properties på jvm-en.
Ved å gjøre dette, unngår man unødvendig service loading: 

`javax.xml.parsers.DocumentBuilderFactory=
com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl`

`javax.xml.transform.TransformerFactory=
com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl`

`com.sun.org.apache.xalan.internal.xsltc.dom.XSLTCDTMManager=
com.sun.org.apache.xalan.internal.xsltc.dom.XSLTCDTMManager`
