package no.difi.sdp.client2.internal;

import no.difi.sdp.client2.domain.exceptions.SendIOException;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Properties;

public class AddClientVersionInterceptor implements HttpRequestInterceptor {

    private static final String CLIENT_VERSION; static {
        try (InputStream resourceAsStream = AddClientVersionInterceptor.class.getResourceAsStream("/project.properties")) {
            Properties properties = new Properties();
            properties.load(resourceAsStream);
            CLIENT_VERSION = properties.getProperty("version");
        } catch (IOException e) {
            throw new SendIOException(e);
        }
    }

    private final String javaVersion;

    public AddClientVersionInterceptor() {
        String javaVersion = System.getProperty("java.version");
        this.javaVersion = javaVersion != null ? javaVersion : "UNKNOWN";
    }

    @Override
    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
        Header[] headers = request.getHeaders("User-Agent");
        String clientUserAgent = MessageFormat.format("difi-sikker-digital-post-klient-java/{0} (Java/{1})", CLIENT_VERSION, javaVersion);

        if (headers.length == 0) {
            request.addHeader("User-Agent", clientUserAgent);
        } else {
            request.addHeader("User-Agent", headers[0].getValue() + " " + clientUserAgent);
        }
    }
}
