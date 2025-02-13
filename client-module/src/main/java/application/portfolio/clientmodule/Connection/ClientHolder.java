package application.portfolio.clientmodule.Connection;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.Map;

public class ClientHolder {

    private static final HttpClient client;
    private static final Map<String, String> gData = Infrastructure.getGatewayData();


    static {
        client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    private ClientHolder() {}

    public static HttpClient getClient() {
        return client;
    }

    public static HttpRequest.Builder getRequest(URI uri, String method, HttpRequest.BodyPublisher bodyPublisher) {
        return HttpRequest.newBuilder(uri)
                .method(method, bodyPublisher)
                .timeout(Duration.ofSeconds(10))
                .version(HttpClient.Version.HTTP_2);
    }

    public static HttpRequest.Builder prepareRequest(String params, String endpoint, String method,
                                                     HttpRequest.BodyPublisher bodyPublisher) {
        String spec = Infrastructure.uriSpecificPart(gData, endpoint, params);

        URI baseUri = Infrastructure.getBaseUri(spec);
        return getRequest(baseUri, method, bodyPublisher);
    }
}
