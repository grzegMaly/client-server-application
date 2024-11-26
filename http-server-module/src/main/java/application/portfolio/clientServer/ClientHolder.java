package application.portfolio.clientServer;

import java.net.URI;
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

    private ClientHolder() {}

    public static HttpClient getClient() {
        return client;
    }

    public static HttpResponse<byte[]> sendGetRequest(URI baseUri) {

        HttpRequest request = HttpRequest.newBuilder(baseUri)
                .GET()
                .timeout(Duration.ofSeconds(10))
                .header("Accept", "application/json")
                .build();

        return handleRequestSend(request);
    }

    public static HttpResponse<byte[]> sendPostRequest(URI baseUri, byte[] data) {

        HttpRequest request = HttpRequest.newBuilder(baseUri)
                .POST(HttpRequest.BodyPublishers.ofByteArray(data))
                .timeout(Duration.ofSeconds(10))
                .header("Accept", "application/json")
                .headers("Content-Type", "application/json")
                .build();

        return handleRequestSend(request);
    }

    public static HttpResponse<byte[]> sendDeleteRequest(URI baseUri) {

        HttpRequest request = HttpRequest.newBuilder(baseUri)
                .DELETE()
                .timeout(Duration.ofSeconds(10))
                .header("Accept", "application/json")
                .build();

        return handleRequestSend(request);
    }

    private static HttpResponse<byte[]> handleRequestSend(HttpRequest request) {
        HttpResponse<byte[]> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
        } catch (Exception e) {
            return null;
        }
        return response;
    }
}
