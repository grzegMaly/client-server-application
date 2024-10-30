package application.portfolio.endpoints.endpointClasses.session;

import application.portfolio.clientServer.ClientHolder;
import application.portfolio.endpoints.EndpointHandler;
import application.portfolio.endpoints.EndpointInfo;
import application.portfolio.utils.Infrastructure;
import application.portfolio.utils.JsonBodyHandler;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

import static java.net.HttpURLConnection.*;

@EndpointInfo(path = "/login")
public class LoginEndpoint implements EndpointHandler, HttpHandler {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final JsonBodyHandler handler;

    static {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        handler = JsonBodyHandler.create(objectMapper);
    }

    @Override
    public HttpHandler endpoint() {
        return this;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if ("POST".equals(exchange.getRequestMethod())) {

            HttpClient client = ClientHolder.getClient();
            Map<String, String> authData = Infrastructure.getAuthorizationData();
            URI baseUri = Infrastructure.getBaseUri(authData).resolve("/authorization");

            HttpRequest request = HttpRequest.newBuilder(baseUri)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(exchange.getRequestBody().readAllBytes()))
                    .header("Content-Type", "application/json")
                    .headers("Accept", "application/json")
                    .timeout(Duration.ofSeconds(10))
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofByteArray())
                    .thenAccept(response -> sendResponse(response, exchange))
                    .exceptionally(ex -> handleError(exchange, ex));
        } else {
            exchange.sendResponseHeaders(HTTP_BAD_GATEWAY, -1);
            exchange.close();
        }
    }

    private Void handleError(HttpExchange exchange, Throwable ex) {
        try (exchange) {
            exchange.sendResponseHeaders(500, -1);
        } catch (IOException e) {
            //Todo: Improve
            e.printStackTrace();
        }
        return null;
    }

    private void sendResponse(HttpResponse<byte[]> response, HttpExchange exchange) {
        try (exchange) {
            if (response != null) {
                int statusCode = response.statusCode();
                byte[] responseData = response.body();

                response.headers().map().forEach((key, value) -> {
                    value.forEach(v -> exchange.getResponseHeaders().add(key, v));
                });
                exchange.sendResponseHeaders(statusCode, responseData.length);
                exchange.getResponseBody().write(responseData);

            }
        } catch (IOException e) {
            //Todo: Improve
            e.printStackTrace();
        }
    }
}