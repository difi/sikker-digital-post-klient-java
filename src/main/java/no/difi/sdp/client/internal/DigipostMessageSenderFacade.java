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
