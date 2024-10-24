package application.portfolio.endpoints.endpointClasses;

import application.portfolio.endpoints.EndpointHandler;
import application.portfolio.endpoints.EndpointInfo;
import application.portfolio.utils.JsonBodyHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;

import static java.net.HttpURLConnection.*;

@EndpointInfo(path = "/login")
public class LoginEndpoint implements EndpointHandler, HttpHandler {

    @Override
    public HttpHandler endpoint() {
        return this;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if ("POST".equals(exchange.getRequestMethod())) {

            /*HttpResponse<String> response;
            CompletableFuture<HttpResponse<String>> responseFuture;
            HttpClient client = ClientHolder.getClient();
            Map<String, String> authData = Infrastructure.getAuthorizationData();
            URI baseUri = Infrastructure.getBaseUri(authData).resolve("/auth");

            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofByteArray(exchange.getRequestBody().readAllBytes()))
                    .uri(baseUri)
                    .header("Accept", "application/json")
                    .build();

            responseFuture = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

            while ((response = responseFuture.getNow(null)) == null) {
                continue;
            }

            if (response.statusCode() != HTTP_OK) {
                String responseMessage = "{Access Denied}";
                exchange.sendResponseHeaders(HTTP_UNAUTHORIZED, responseMessage.length());
                exchange.getResponseBody().write(responseMessage.getBytes());
            } else {
                String data = response.body();
                exchange.sendResponseHeaders(HTTP_OK, data.length());
                exchange.getResponseBody().write(data.getBytes());
            }*/

            manageTempAuthorization(exchange);
        } else {
            exchange.sendResponseHeaders(HTTP_BAD_GATEWAY, -1);
        }
        exchange.close();
    }

    private void manageTempAuthorization(HttpExchange exchange) {

        ObjectMapper objectMapper = new ObjectMapper();
        var handler = JsonBodyHandler.create(objectMapper);

        InputStream stream = exchange.getRequestBody();
        try {
            JsonNode node = objectMapper.readTree(stream);
            System.out.println(node);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}