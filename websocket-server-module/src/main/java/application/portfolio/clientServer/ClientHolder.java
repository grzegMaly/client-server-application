package application.portfolio.clientServer;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static java.net.HttpURLConnection.HTTP_OK;

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

    public static boolean handlePostRequest(byte[] data, URI baseUri) {

        HttpRequest request = HttpRequest.newBuilder(baseUri)
                .POST(HttpRequest.BodyPublishers.ofByteArray(data))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .build();

        return handleRequestSend(request);
    }

    private static boolean handleRequestSend(HttpRequest request) {

        HttpResponse<byte[]> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
        } catch (Exception e) {
            return false;
        }

        return response.statusCode() == HTTP_OK;
    }
}
