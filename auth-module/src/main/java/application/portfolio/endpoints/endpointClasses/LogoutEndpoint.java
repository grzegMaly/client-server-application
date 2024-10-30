package application.portfolio.endpoints.endpointClasses;

import application.portfolio.endpoints.EndpointHandler;
import application.portfolio.endpoints.EndpointInfo;
import application.portfolio.token.AuthService;
import application.portfolio.response.ResponseHandler;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;


@EndpointInfo(path = "/logout")
public class LogoutEndpoint implements EndpointHandler, HttpHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AuthService authService = new AuthService();

    public LogoutEndpoint() {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Override
    public HttpHandler endpoint() {
        return this;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if ("POST".equals(exchange.getRequestMethod())) {

            String data = new String(exchange.getRequestBody().readAllBytes());
            String token = exchange.getRequestHeaders().getFirst("Authorization");
            JsonNode node = objectMapper.readTree(data);

            var response = authService.deactivate(token, node);
            ResponseHandler.sendResponse(exchange, response);
        }
    }
}
