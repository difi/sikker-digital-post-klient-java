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

import no.difi.sdp.client.ExceptionMapper;
import no.difi.sdp.client.KlientKonfigurasjon;
import no.difi.sdp.client.domain.TekniskAvsender;
import no.difi.sdp.client.domain.exceptions.SendException;
import no.digipost.api.MessageSender;
import no.digipost.api.interceptors.KeyStoreInfo;
import no.digipost.api.interceptors.TransactionLogClientInterceptor;
import no.digipost.api.interceptors.WsSecurityInterceptor;
import no.digipost.api.representations.EbmsAktoer;
import no.digipost.api.representations.EbmsApplikasjonsKvittering;
import no.digipost.api.representations.EbmsForsendelse;
import no.digipost.api.representations.EbmsPullRequest;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;

import static no.difi.sdp.client.domain.exceptions.SendException.AntattSkyldig.UKJENT;

public class DigipostMessageSenderFacade {

    private final MessageSender messageSender;
    private ExceptionMapper exceptionMapper = new ExceptionMapper();

    public DigipostMessageSenderFacade(TekniskAvsender avsender, KlientKonfigurasjon konfigurasjon) {
        KeyStoreInfo keyStoreInfo = avsender.getNoekkelpar().getKeyStoreInfo();
        WsSecurityInterceptor wsSecurityInterceptor = new WsSecurityInterceptor(keyStoreInfo, new UserFriendlyWsSecurityExceptionMapper());
        wsSecurityInterceptor.afterPropertiesSet();

        MessageSender.Builder messageSenderBuilder = MessageSender.create(konfigurasjon.getMeldingsformidlerRoot().toString(),
                keyStoreInfo,
                wsSecurityInterceptor,
                EbmsAktoer.avsender(avsender.getOrganisasjonsnummer()),
                EbmsAktoer.meldingsformidler(konfigurasjon.getMeldingsformidlerOrganisasjon()))
                .withConnectTimeout((int) konfigurasjon.getConnectTimeoutInMillis())
                .withSocketTimeout((int) konfigurasjon.getSocketTimeoutInMillis())
                .withConnectionRequestTimeout((int) konfigurasjon.getConnectionRequestTimeoutInMillis())
                .withDefaultMaxPerRoute(konfigurasjon.getMaxConnectionPoolSize()) // Vi vil i praksis bare kjøre én route med denne klienten.
                .withMaxTotal(konfigurasjon.getMaxConnectionPoolSize());

        if (konfigurasjon.useProxy()) {
            messageSenderBuilder.withHttpProxy(konfigurasjon.getProxyHost(), konfigurasjon.getProxyPort());
        }

        messageSenderBuilder.withHttpRequestInterceptors(new AddClientVersionInterceptor());

        for (ClientInterceptor clientInterceptor : konfigurasjon.getInterceptors()) {
            // TransactionLogClientInterceptoren bør alltid ligge ytterst for å sikre riktig transaksjonslogging (i tilfelle en custom interceptor modifiserer requestet)
            messageSenderBuilder.withMeldingInterceptorBefore(TransactionLogClientInterceptor.class, clientInterceptor);
        }

        messageSender = messageSenderBuilder.build();
    }

    public void send(final EbmsForsendelse ebmsForsendelse) {
        performRequest(new VoidRequest() {
            @Override
            public void exec() {
                messageSender.send(ebmsForsendelse);
            }
        });
    }

    public EbmsApplikasjonsKvittering hentKvittering(final EbmsPullRequest ebmsPullRequest) {
        return performRequest(new Request<EbmsApplikasjonsKvittering>() {
            @Override
            public EbmsApplikasjonsKvittering exec() {
                return messageSender.hentKvittering(ebmsPullRequest);
            }
        });
    }

    public EbmsApplikasjonsKvittering hentKvittering(final EbmsPullRequest ebmsPullRequest, final EbmsApplikasjonsKvittering applikasjonsKvittering) {
        return performRequest(new Request<EbmsApplikasjonsKvittering>() {
            @Override
            public EbmsApplikasjonsKvittering exec() {
                return messageSender.hentKvittering(ebmsPullRequest, applikasjonsKvittering);
            }
        });
    }

    public void bekreft(final EbmsApplikasjonsKvittering kvittering) {
        performRequest(new VoidRequest() {
            @Override
            public void exec() {
                messageSender.bekreft(kvittering);
            }
        });

    }

    private void performRequest(final VoidRequest request) {
        this.performRequest(new Request<Object>() {
            @Override
            public Object exec() {
                request.exec();
                return null;
            }
        });
    }

    private <T> T performRequest(Request<T> request) throws SendException {
        try {
            return request.exec();
        }
        catch (RuntimeException e) {
            RuntimeException mappedException = exceptionMapper.mapException(e);
            if (mappedException != null) {
                throw mappedException;
            }

            throw new SendException("An unhandled exception occured while performing request", UKJENT, e);
        }
    }

    private interface VoidRequest {
        void exec();
    }

    private interface Request<T> {
        T exec();
    }

    public void setExceptionMapper(ExceptionMapper exceptionMapper) {
        this.exceptionMapper = exceptionMapper;
    }
}
