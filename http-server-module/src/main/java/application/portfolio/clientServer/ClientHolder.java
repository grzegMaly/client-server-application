package application.portfolio.clientServer;


import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ClientHolder {

    private static final HttpClient client;

    static {
        client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    private ClientHolder() {
    }

    public static HttpClient getClient() {
        return client;
    }

    public static <T> HttpResponse<T> handleRequestSend(HttpRequest request, HttpResponse.BodyHandler<T> bodyHandler) {
        HttpResponse<T> response;
        try {
            response = client.send(request, bodyHandler);
        } catch (Exception e) {
            return null;
        }
        return response;
    }
}