package application.portfolio.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class ResponseHandler {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void sendResponse(HttpExchange exchange, AuthTokenResponse tokenResponse) throws IOException {

        if (tokenResponse.getAuthToken() != null) {
            exchange.getResponseHeaders().set("Authorization", "Bearer " + tokenResponse.getAuthToken().getToken().toString());
        }

        byte[] jsonResponse = objectMapper.writeValueAsBytes(tokenResponse.getResponseNode());
        exchange.sendResponseHeaders(tokenResponse.getStatusCode(), jsonResponse.length);
        exchange.getResponseBody().write(jsonResponse);
        exchange.close();
    }
}
