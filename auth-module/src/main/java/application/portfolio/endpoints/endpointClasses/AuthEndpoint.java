package application.portfolio.endpoints.endpointClasses;

import application.portfolio.endpoints.EndpointHandler;
import application.portfolio.endpoints.EndpointInfo;
import application.portfolio.response.AuthTokenResponse;
import application.portfolio.response.ResponseHandler;
import application.portfolio.token.AuthService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

import static java.net.HttpURLConnection.*;

@EndpointInfo(path = "/authorization")
public class AuthEndpoint implements EndpointHandler, HttpHandler {

    private final AuthService authService = new AuthService();


    @Override
    public HttpHandler endpoint() {
        return this;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        try {
            if ("POST".equals(exchange.getRequestMethod())) {
                byte[] data = exchange.getRequestBody().readAllBytes();

                AuthTokenResponse response = authService.authorize(data);
                ResponseHandler.sendResponse(exchange, response);
            } else {
                AuthTokenResponse response = new AuthTokenResponse("Method Not Allowed", HTTP_BAD_REQUEST);
                ResponseHandler.sendResponse(exchange, response);
            }
        } catch (IOException e) {
            exchange.sendResponseHeaders(HTTP_INTERNAL_ERROR, -1);
        } finally {
            exchange.close();
        }
    }
}
