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

import no.difi.sdp.client.KlientKonfigurasjon;
import no.difi.sdp.client.domain.Avsender;
import no.digipost.api.MessageSender;
import no.digipost.api.interceptors.KeyStoreInfo;
import no.digipost.api.interceptors.WsSecurityInterceptor;
import no.digipost.api.representations.EbmsAktoer;
import no.digipost.api.representations.EbmsApplikasjonsKvittering;
import no.digipost.api.representations.EbmsForsendelse;
import no.digipost.api.representations.EbmsPullRequest;

public class DigipostMessageSenderFacade {

    private final MessageSender messageSender;

    public DigipostMessageSenderFacade(Avsender avsender, KlientKonfigurasjon konfigurasjon) {

        KeyStoreInfo keyStoreInfo = avsender.getNoekkelpar().getKeyStoreInfo();
        WsSecurityInterceptor wsSecurityInterceptor = new WsSecurityInterceptor(keyStoreInfo, new UserFriendlyWsSecurityExceptionMapper());
        wsSecurityInterceptor.afterPropertiesSet();

        MessageSender.Builder msBuilder = MessageSender.create(konfigurasjon.getMeldingsformidlerRoot().toString(),
                keyStoreInfo,
                wsSecurityInterceptor,
                EbmsAktoer.avsender(avsender.getOrganisasjonsnummer()),
                EbmsAktoer.meldingsformidler(konfigurasjon.getMeldingsformidlerOrganisasjon()))
                .withConnectTimeout((int) konfigurasjon.getConnectTimeoutInMillis())
                .withSocketTimeout((int) konfigurasjon.getSocketTimeoutInMillis())
                .withConnectionRequestTimeout((int) konfigurasjon.getConnectionRequestTimeoutInMillis());

        if (konfigurasjon.useProxy()) {
            msBuilder.withHttpProxy(konfigurasjon.getProxyHost(), konfigurasjon.getProxyPort());
        }

        messageSender = msBuilder.build();
    }

    public void send(EbmsForsendelse ebmsForsendelse) {
        messageSender.send(ebmsForsendelse);
    }

    public EbmsApplikasjonsKvittering hentKvittering(EbmsPullRequest ebmsPullRequest) {
        return messageSender.hentKvittering(ebmsPullRequest);
    }

    public EbmsApplikasjonsKvittering hentKvittering(EbmsPullRequest ebmsPullRequest, EbmsApplikasjonsKvittering applikasjonsKvittering) {
        return messageSender.hentKvittering(ebmsPullRequest, applikasjonsKvittering);
    }

    public void bekreft(EbmsApplikasjonsKvittering kvittering) {
        messageSender.bekreft(kvittering);
    }
}
