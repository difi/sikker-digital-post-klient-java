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
import no.digipost.api.PMode;

import java.util.UUID;

import static no.difi.sdp.client.domain.Forsendelse.Type.DIGITAL;
import static no.difi.sdp.client.domain.Forsendelse.Type.FYSISK;

public class Forsendelse {

	public enum Type {
		DIGITAL(PMode.Action.FORMIDLE_DIGITAL),
        FYSISK(PMode.Action.FORMIDLE_FYSISK);

        public final PMode.Action action;

        Type(PMode.Action action) {
            this.action = action;
        }
    }

	public final Type type;
    private final DigitalPost digitalPost;
    private final FysiskPost fysiskPost;
    private final Dokumentpakke dokumentpakke;
    private final Behandlingsansvarlig behandlingsansvarlig;
    private String konversasjonsId = UUID.randomUUID().toString();
    private Prioritet prioritet = Prioritet.NORMAL;
    private String spraakkode = "NO";
    private String mpcId;

    private Forsendelse(Behandlingsansvarlig behandlingsansvarlig, DigitalPost digitalPost, Dokumentpakke dokumentpakke) {
    	this.type = DIGITAL;
        this.behandlingsansvarlig = behandlingsansvarlig;
        this.digitalPost = digitalPost;
        this.fysiskPost = null;
        this.dokumentpakke = dokumentpakke;
    }

    public Forsendelse(Behandlingsansvarlig behandlingsansvarlig, FysiskPost fysiskPost, Dokumentpakke dokumentpakke) {
    	this.type = FYSISK;
    	this.behandlingsansvarlig = behandlingsansvarlig;
    	this.dokumentpakke = dokumentpakke;
    	this.fysiskPost = fysiskPost;
    	this.digitalPost = null;
    }

	public String getKonversasjonsId() {
        return konversasjonsId;
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

    public String getMpcId() {
        return mpcId;
    }

    public Behandlingsansvarlig getBehandlingsansvarlig() {
        return behandlingsansvarlig;
    }

    /**
     * @param behandlingsansvarlig Ansvarlig avsender av forsendelsen. Dette vil i de aller fleste tilfeller være
     *                             den offentlige virksomheten som er ansvarlig for brevet som skal sendes.
     * @param digitalPost Informasjon som brukes av postkasseleverandør for å behandle den digitale posten.
     * @param dokumentpakke Pakke med hoveddokument og evt vedlegg som skal sendes.
     */
    public static Builder digital(Behandlingsansvarlig behandlingsansvarlig, DigitalPost digitalPost, Dokumentpakke dokumentpakke) {
        return new Builder(behandlingsansvarlig, digitalPost, dokumentpakke);
    }

	public static Builder fysisk(Behandlingsansvarlig behandlingsansvarlig, FysiskPost fysiskPost, Dokumentpakke dokumentpakke) {
	    return new Builder(behandlingsansvarlig, fysiskPost, dokumentpakke);
    }

    public static class Builder {

        private final Forsendelse target;
        private boolean built = false;

        private Builder(Behandlingsansvarlig behandlingsansvarlig, DigitalPost digitalPost, Dokumentpakke dokumentpakke) {
            this.target = new Forsendelse(behandlingsansvarlig, digitalPost, dokumentpakke);
        }

        private Builder(Behandlingsansvarlig behandlingsansvarlig, FysiskPost fysiskPost, Dokumentpakke dokumentpakke) {
            this.target = new Forsendelse(behandlingsansvarlig, fysiskPost, dokumentpakke);
        }

        /**
         * Unik ID opprettet og definert i en initiell melding og siden bruk i alle tilhørende kvitteringer knyttet til den opprinnelige meldingen.
         * Skal være unik for en avsender.
         *
         * Standard er {@link java.util.UUID#randomUUID()}}.
         */
        public Builder konversasjonsId(String konversasjonsId) {
            target.konversasjonsId = konversasjonsId;
            return this;
        }

        /**
         * Standard er {@link no.difi.sdp.client.domain.Prioritet#NORMAL}
         */
        public Builder prioritet(Prioritet prioritet) {
            target.prioritet = prioritet;
            return this;
        }

        /**
         * Språkkode i henhold til ISO-639-1 (2 bokstaver). Brukes til å informere postkassen om hvilket språk som benyttes, slik at varselet om mulig kan vises i riktig språkkontekst.
         *
         * Standard er NO.
         */
        public Builder spraakkode(String spraakkode) {
            target.spraakkode = spraakkode;
            return this;
        }

        /**
         * Brukes til å skille mellom ulike kvitteringskøer for samme tekniske avsender. En forsendelse gjort med en
         * MPC Id vil kun dukke opp i kvitteringskøen med samme MPC Id.
         *
         * Standardverdi er blank MPC Id.
         *
         * @see no.difi.sdp.client.domain.kvittering.KvitteringForespoersel.Builder#mpcId(String)
         */
        public Builder mpcId(String mpcId) {
            target.mpcId = mpcId;
            return this;
        }

        public Forsendelse build() {
            if (built) throw new IllegalStateException("Can't build twice");
            built = true;
            return target;
        }
    }

	public TekniskMottaker getTekniskMottaker() {
		switch (type) {
    		case DIGITAL: return digitalPost.getMottaker().getMottakersPostkasse();
    		case FYSISK: return fysiskPost.getUtskriftsleverandoer();
    		default: throw new IllegalStateException("Forsendelse av type " + type + " har ikke teknisk mottaker");
		}
    }

}
