package no.difi.sdp.client2.internal;

import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.EndpointExceptionResolver;

/**
 * This Exception Resolver does nothing whatsoever. It merely used to emphasize that the {@link no.digipost.api.interceptors.WsSecurityInterceptor WsSecurityInterceptor} should
 * not do any exception resolving as this ought to be done in {@link no.difi.sdp.client2.ExceptionMapper ExceptionMapper}.
 */
class NoOpExceptionResolver implements EndpointExceptionResolver {
    @Override
    public boolean resolveException(MessageContext messageContext, Object o, Exception e) {
        return false;
    }
}
