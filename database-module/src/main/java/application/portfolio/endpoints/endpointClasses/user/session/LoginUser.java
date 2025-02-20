package application.portfolio.endpoints.endpointClasses.user.session;

import application.portfolio.endpoints.EndpointHandler;
import application.portfolio.endpoints.EndpointInfo;
import application.portfolio.endpoints.endpointClasses.user.userUtils.UserGetMethods;
import application.portfolio.clientServer.response.PersonResponse;
import application.portfolio.utils.DataParser;
import application.portfolio.utils.ResponseHandler;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Map;

import static java.net.HttpURLConnection.*;

@EndpointInfo(path = "/user/login")
public class LoginUser implements EndpointHandler, HttpHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Override
    public HttpHandler endpoint() {
        return this;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        try (exchange) {
            if (!"POST".equals(exchange.getRequestMethod())) {
                ResponseHandler.handleError(exchange, "Invalid request method used", HTTP_BAD_REQUEST);
            }

            JsonNode node = DataParser.convertToNode(exchange);

            PersonResponse personResponse = UserGetMethods.getPersonFromDatabase(node);
            Map.Entry<Integer, JsonNode> responseNode = personResponse.toJsonResponse();

            ResponseHandler.sendResponse(exchange, responseNode);
        } catch (Exception e) {
            exchange.sendResponseHeaders(HTTP_INTERNAL_ERROR, -1);
        }
    }
}