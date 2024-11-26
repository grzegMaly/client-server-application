package application.portfolio.clientServer;

import java.net.http.HttpClient;
import java.time.Duration;

public class ClientHolder {

    private static final HttpClient client;

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
}