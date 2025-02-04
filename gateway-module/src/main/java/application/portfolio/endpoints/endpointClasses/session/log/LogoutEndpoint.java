package application.portfolio.endpoints.endpointClasses.session.log;

import application.portfolio.clientServer.ClientHolder;
import application.portfolio.endpoints.EndpointHandler;
import application.portfolio.endpoints.EndpointInfo;
import application.portfolio.utils.Infrastructure;
import application.portfolio.requestResponse.ResponseHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_BAD_GATEWAY;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;

@EndpointInfo(path = "/user/logout")
public class LogoutEndpoint implements EndpointHandler, HttpHandler {
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
                String spec = Infrastructure.uriSpecificPart(authData, "logout");
                URI baseUri = Infrastructure.getBaseUri(authData).resolve(spec);

                String token = exchange.getRequestHeaders().getFirst("Authorization");
                HttpRequest request = HttpRequest.newBuilder(baseUri)
                        .POST(HttpRequest.BodyPublishers.ofByteArray(exchange.getRequestBody().readAllBytes()))
                        .header("Authorization", token)
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
