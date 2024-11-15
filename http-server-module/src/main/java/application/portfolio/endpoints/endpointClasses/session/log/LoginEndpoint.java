package application.portfolio.endpoints.endpointClasses.session.log;

import application.portfolio.clientServer.ClientHolder;
import application.portfolio.endpoints.EndpointHandler;
import application.portfolio.endpoints.EndpointInfo;
import application.portfolio.utils.Infrastructure;
import application.portfolio.utils.ResponseHandler;
import com.fasterxml.jackson.annotation.JsonInclude;
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

    static {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Override
    public HttpHandler endpoint() {
        return this;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        try (exchange) {
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

                HttpResponse<byte[]> response;
                try {
                    response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
                    ResponseHandler.sendResponse(response, exchange);
                } catch (Exception e) {
                    ResponseHandler.handleError(exchange, "Unknown Error", HTTP_INTERNAL_ERROR);
                }
            } else {
                ResponseHandler.handleError(exchange, "Bad Gateway", HTTP_BAD_GATEWAY);
            }
        } catch (IOException e) {
            ResponseHandler.handleError(exchange, "Unknown Error", HTTP_INTERNAL_ERROR);
        }
    }
}