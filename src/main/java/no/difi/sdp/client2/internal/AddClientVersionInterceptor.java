package no.difi.sdp.client2.internal;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.text.MessageFormat;

public class AddClientVersionInterceptor implements HttpRequestInterceptor {

    private final String clientVersion;
    private final String javaVersion;

    public AddClientVersionInterceptor() {
        String implementationVersion = getClass().getPackage().getImplementationVersion();
        String javaVersion = System.getProperty("java.version");
        this.clientVersion = implementationVersion != null ? implementationVersion : System.getProperty("user.name");
        this.javaVersion = javaVersion != null ? javaVersion : "UNKNOWN";
    }

    @Override
    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
        Header[] headers = request.getHeaders("User-Agent");
        String clientUserAgent = MessageFormat.format("difi-sikker-digital-post-klient-java/{0} (Java/{1})", clientVersion, javaVersion);

        if (headers.length == 0) {
            request.addHeader("User-Agent", clientUserAgent);
        } else {
            request.addHeader("User-Agent", headers[0].getValue() + " " + clientUserAgent);
        }
    }
}
