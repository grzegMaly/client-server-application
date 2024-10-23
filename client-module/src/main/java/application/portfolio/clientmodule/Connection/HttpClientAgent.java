package application.portfolio.clientmodule.Connection;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.net.HttpURLConnection.HTTP_OK;

public class HttpClientAgent {

    private static HttpClient INSTANCE = null;

    private HttpClientAgent() {
     }

    public static HttpClient getHttpClient() {
        if (INSTANCE == null) {
            INSTANCE = HttpClient.newHttpClient();
        }
        return INSTANCE;
    }

    public static boolean ping() {

        HttpResponse<String> response;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(new URI("http://localhost:2137/ping"))
                    .build();

            response = INSTANCE.send(request,
                    HttpResponse.BodyHandlers.ofString());
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return response != null && response.statusCode() == HTTP_OK;
    }
}