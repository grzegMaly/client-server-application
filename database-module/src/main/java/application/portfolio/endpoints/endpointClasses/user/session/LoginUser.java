package application.portfolio.endpoints.endpointClasses.user.session;

import application.portfolio.endpoints.EndpointHandler;
import application.portfolio.endpoints.EndpointInfo;
import application.portfolio.endpoints.endpointClasses.user.userUtils.UserGetMethods;
import application.portfolio.objects.model.Person.PersonResponse;
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

        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_ABSENT);
        try (exchange) {
            if (!"POST".equals(exchange.getRequestMethod())) {
                ResponseHandler.handleError(exchange, "Invalid request method used", HTTP_BAD_REQUEST);
            }

            var body = exchange.getRequestBody();
            JsonNode node = objectMapper.readTree(body);

            PersonResponse personResponse = UserGetMethods.getPersonFromDatabase(node);
            Map.Entry<Integer, JsonNode> responseNode = PersonResponse.toPersonJsonResponse(personResponse);

            ResponseHandler.sendResponse(exchange, responseNode);
        } catch (IOException e) {
            exchange.sendResponseHeaders(HTTP_INTERNAL_ERROR, -1);
        }
    }
}