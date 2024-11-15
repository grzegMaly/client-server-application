package application.portfolio.endpoints.endpointClasses.user.session;

import application.portfolio.endpoints.EndpointHandler;
import application.portfolio.endpoints.EndpointInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_OK;

@EndpointInfo(path = "/user/register")
public class RegisterUser implements EndpointHandler, HttpHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public HttpHandler endpoint() {
        return this;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        try {
            if ("POST".equals(exchange.getRequestMethod())) {
                String elo = "ELO";
                ObjectNode objectNode = objectMapper.createObjectNode();
                objectNode.put("response", elo);
                byte[] data = objectMapper.writeValueAsBytes(objectNode);
                exchange.sendResponseHeaders(HTTP_OK, data.length);
                exchange.getResponseBody().write(data);
            }
        } catch (IOException e) {
            exchange.sendResponseHeaders(HTTP_INTERNAL_ERROR, -1);
        } finally {
            exchange.close();
        }
    }
}
