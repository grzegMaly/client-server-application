package application.portfolio.endpoints.endpointClasses;

import application.portfolio.endpoints.EndpointHandler;
import application.portfolio.endpoints.EndpointInfo;
import application.portfolio.utils.Infrastructure;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_BAD_GATEWAY;

@EndpointInfo(path = "/login")
public class LoginEndpoint implements EndpointHandler, HttpHandler {

    @Override
    public HttpHandler endpoint() {
        return this;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if ("POST".equals(exchange.getRequestMethod())) {

            HttpResponse<String> response;
            Map<String, String> authData = Infrastructure.getAuthorizationData();
            URI baseUri = Infrastructure.getBaseUri(authData).resolve("/auth");

            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(baseUri)
                    .build();

        } else {
            exchange.sendResponseHeaders(HTTP_BAD_GATEWAY, -1);
        }
        exchange.close();
    }
}