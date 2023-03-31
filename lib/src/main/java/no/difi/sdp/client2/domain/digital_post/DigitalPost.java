/*
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
package no.difi.sdp.client2.domain.digital_post;

import no.difi.sdp.client2.domain.Mottaker;

import java.util.Date;

public class DigitalPost {

    private Mottaker mottaker;
    private Date virkningsdato;
    private boolean aapningskvittering;
    private Sikkerhetsnivaa sikkerhetsnivaa = Sikkerhetsnivaa.NIVAA_4;
    private String ikkeSensitivTittel;
    private EpostVarsel epostVarsel;
    private SmsVarsel smsVarsel;

    private DigitalPost(Mottaker mottaker, String ikkeSensitivTittel) {
        this.mottaker = mottaker;
        this.ikkeSensitivTittel = ikkeSensitivTittel;
    }

    public Mottaker getMottaker() {
        return mottaker;
    }

    public Date getVirkningsdato() {
        return virkningsdato;
    }

    public boolean isAapningskvittering() {
        return aapningskvittering;
    }

    public Sikkerhetsnivaa getSikkerhetsnivaa() {
        return sikkerhetsnivaa;
    }

    public String getIkkeSensitivTittel() {
        return ikkeSensitivTittel;
    }

    public EpostVarsel getEpostVarsel() {
        return epostVarsel;
    }

    public SmsVarsel getSmsVarsel() {
        return smsVarsel;
    }

    /**
     * @param mottaker           Mottaker av digital post.
     * @param ikkeSensitivTittel Ikke-sensitiv tittel på brevet.
     *                           Denne tittelen vil være synlig under transport av meldingen og kan vises i mottakerens postkasse selv om det ikke er autentisert med tilstrekkelig autentiseringsnivå.
     */
    public static Builder builder(Mottaker mottaker, String ikkeSensitivTittel) {
        return new Builder(mottaker, ikkeSensitivTittel);
    }

    public static class Builder {

        private final DigitalPost target;
        private boolean built = false;

        private Builder(Mottaker mottaker, String ikkeSensitivTittel) {
            target = new DigitalPost(mottaker, ikkeSensitivTittel);
        }

        /**
         * Når brevet tilgjengeliggjøres for mottaker.
         * <p>
         * Standard er nå.
         */
        public Builder virkningsdato(Date virkningsdato) {
            target.virkningsdato = virkningsdato;
            return this;
        }

        /**
         * Ønskes kvittering når brevet blir åpnet av mottaker?
         * <p>
         * Standard er false.
         */
        public Builder aapningskvittering(boolean aapningskvittering) {
            target.aapningskvittering = aapningskvittering;
            return this;
        }

        /**
         * Nødvendig autentiseringsnivå som kreves av mottaker i postkassen for å åpne brevet.
         * <p>
         * Standard er {@link Sikkerhetsnivaa#NIVAA_4}.
         */
        public Builder sikkerhetsnivaa(Sikkerhetsnivaa sikkerhetsnivaa) {
            target.sikkerhetsnivaa = sikkerhetsnivaa;
            return this;
        }

        /**
         * Minimum e-postvarsel som skal sendes til mottaker av brevet. Postkassen kan velge å sende andre varsler i tillegg.
         * <p>
         * Standard er standardoppførselen til postkasseleverandøren.
         */
        public Builder epostVarsel(EpostVarsel epostVarsel) {
            target.epostVarsel = epostVarsel;
            return this;
        }

        /**
         * Minimum sms-varsel som skal sendes til mottaker av brevet. Postkassen kan velge å sende andre varsler i tillegg.
         * <p>
         * Standard er standardoppførselen til postkasseleverandøren.
         */
        public Builder smsVarsel(SmsVarsel smsVarsel) {
            target.smsVarsel = smsVarsel;
            return this;
        }

        public DigitalPost build() {
            if (built) throw new IllegalStateException("Can't build twice");
            built = true;

            return target;
        }
    }
}
