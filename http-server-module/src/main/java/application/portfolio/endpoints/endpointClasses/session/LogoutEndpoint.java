package application.portfolio.endpoints.endpointClasses.session;

import application.portfolio.clientServer.ClientHolder;
import application.portfolio.endpoints.EndpointHandler;
import application.portfolio.endpoints.EndpointInfo;
import application.portfolio.utils.Infrastructure;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_BAD_GATEWAY;

@EndpointInfo(path = "/logout")
public class LogoutEndpoint implements EndpointHandler, HttpHandler {
    @Override
    public HttpHandler endpoint() {
        return this;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if ("POST".equals(exchange.getRequestMethod())) {

            HttpClient client = ClientHolder.getClient();
            Map<String, String> authData = Infrastructure.getAuthorizationData();
            URI baseUri = Infrastructure.getBaseUri(authData).resolve("/logout");

            String token = exchange.getRequestHeaders().getFirst("Authorization");
            HttpRequest request = HttpRequest.newBuilder(baseUri)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(exchange.getRequestBody().readAllBytes()))
                    .header("Authorization", token)
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
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

    private void sendResponse(HttpResponse<String> response, HttpExchange exchange) {
        try (exchange) {
            if (response != null) {
                int statusCode = response.statusCode();
                byte[] responseData = response.body().getBytes();

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
