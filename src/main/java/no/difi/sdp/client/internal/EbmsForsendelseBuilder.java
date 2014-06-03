/**
 * Copyright (C) Posten Norge AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package no.difi.sdp.client.internal;

import no.difi.begrep.sdp.schema_v10.SDPDigitalPost;
import no.difi.sdp.client.domain.Avsender;
import no.difi.sdp.client.domain.Forsendelse;
import no.difi.sdp.client.domain.Mottaker;
import no.posten.dpost.offentlig.api.representations.Dokumentpakke;
import no.posten.dpost.offentlig.api.representations.EbmsAktoer;
import no.posten.dpost.offentlig.api.representations.EbmsForsendelse;
import no.posten.dpost.offentlig.api.representations.Organisasjonsnummer;

public class EbmsForsendelseBuilder {

    private final SDPBuilder sdpBuilder;
    private final CreateDokumentpakke createDokumentpakke;

    public EbmsForsendelseBuilder() {
        sdpBuilder = new SDPBuilder();
        createDokumentpakke = new CreateDokumentpakke();
    }

    public EbmsForsendelse buildEbmsForsendelse(final Avsender avsender, final Organisasjonsnummer digipostMeldingsformidler, final Forsendelse forsendelse) {
        Mottaker mottaker = forsendelse.getDigitalPost().getMottaker();

        EbmsAktoer avsenderAktoer = EbmsAktoer.avsender(avsender.getOrganisasjonsnummer());
        Organisasjonsnummer postkasse = new Organisasjonsnummer(mottaker.getOrganisasjonsnummerPostkasse());

        SDPDigitalPost sikkerDigitalPost = createSikkerDigitalPost(avsender, forsendelse);
        Dokumentpakke dokumentpakke = createDokumentpakke.createDokumentpakke(avsender, forsendelse);

        return EbmsForsendelse.create(avsenderAktoer, EbmsAktoer.meldingsformidler(digipostMeldingsformidler), postkasse, sikkerDigitalPost, dokumentpakke).build();
    }

    private SDPDigitalPost createSikkerDigitalPost(final Avsender avsender, final Forsendelse forsendelse) {
        return sdpBuilder.buildDigitalPost(avsender, forsendelse);
    }
}
