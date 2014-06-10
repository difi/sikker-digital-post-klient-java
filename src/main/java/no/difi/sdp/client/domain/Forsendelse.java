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
package no.difi.sdp.client.domain;

import no.difi.sdp.client.domain.digital_post.DigitalPost;
import no.difi.sdp.client.domain.fysisk_post.FysiskPost;

import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class Forsendelse {

    private DigitalPost digitalPost;
    private FysiskPost fysiskPost;
    private Dokumentpakke dokumentpakke;
    private String konversasjonsId = UUID.randomUUID().toString();
    private Prioritet prioritet = Prioritet.NORMAL;
    private String spraakkode = "NO";

    private Forsendelse(DigitalPost digitalPost, FysiskPost fysiskPost, Dokumentpakke dokumentpakke) {
        this.digitalPost = digitalPost;
        this.fysiskPost = fysiskPost;
        this.dokumentpakke = dokumentpakke;
    }

    public String getKonversasjonsId() {
        return konversasjonsId;
    }

    public boolean isDigitalPostforsendelse() {
        return digitalPost != null;
    }

    public DigitalPost getDigitalPost() {
        return digitalPost;
    }

    public FysiskPost getFysiskPost() {
        return fysiskPost;
    }

    public Dokumentpakke getDokumentpakke() {
        return dokumentpakke;
    }

    public Prioritet getPrioritet() {
        return prioritet;
    }

    public String getSpraakkode() {
        return spraakkode;
    }

    /**
     * @param digitalPost Informasjon som brukes av postkasseleverandør for å behandle den digitale posten.
     * @param dokumentpakke Pakke med hoveddokument og evt vedlegg som skal sendes.
     */
    public static Builder digital(DigitalPost digitalPost, Dokumentpakke dokumentpakke) {
        return new Builder(digitalPost, null, dokumentpakke);
    }

    public static Builder fysisk(FysiskPost fysiskPost, Dokumentpakke dokumentpakke) {
        return new Builder(null, fysiskPost, dokumentpakke);
    }

    public static class Builder {

        private final Forsendelse target;
        private boolean built = false;

        private Builder(DigitalPost digitalPost, FysiskPost fysiskPost, Dokumentpakke dokumentpakke) {
            if ((fysiskPost != null && digitalPost != null) || (fysiskPost == null && digitalPost == null)) {
                throw new IllegalArgumentException("Kan kun være enten fysisk post eller digital post");
            }

            if (dokumentpakke == null) {
                throw new IllegalArgumentException("Kan ikke lage forsendelse uten dokumentpakke");
            }

            this.target = new Forsendelse(digitalPost, fysiskPost, dokumentpakke);
        }

        /**
         * Unik ID opprettet og definert i en initiell melding og siden bruk i alle tilhørende kvitteringer knyttet til den opprinnelige meldingen.
         * Skal være unik for en avsender.
         *
         * Standard er {@link java.util.UUID#randomUUID()}}.
         */
        public Builder konversasjonsId(String konversasjonsId) {
            if (isEmpty(konversasjonsId)) {
                throw new IllegalArgumentException("Konversasjonsid genereres automatisk dersom det ikke angis, må ikke nullstilles");
            }
            target.konversasjonsId = konversasjonsId;
            return this;
        }

        /**
         * Standard er {@link no.difi.sdp.client.domain.Prioritet#NORMAL}
         */
        public Builder prioritet(Prioritet prioritet) {
            if (prioritet == null) {
                throw new IllegalArgumentException("Standard prioritet er NORMAL, kan ikke nullstilles");
            }
            target.prioritet = prioritet;
            return this;
        }

        /**
         * Språkkode i henhold til ISO-639-1 (2 bokstaver). Brukes til å informere postkassen om hvilket språk som benyttes, slik at varselet om mulig kan vises i riktig språkkontekst.
         *
         * Standard er NO.
         */
        public Builder spraakkode(String spraakkode) {
            if (isEmpty(spraakkode)) {
                throw new IllegalArgumentException("Språk settes automatisk til NO dersom det ikke angis, må ikke nullstilles");
            }
            target.spraakkode = spraakkode;
            return this;
        }

        public Forsendelse build() {
            if (built) throw new IllegalStateException("Can't build twice");
            built = true;
            return target;
        }
    }
}
