package application.portfolio.endpoints.endpointClasses.user;

import application.portfolio.endpoints.EndpointHandler;
import application.portfolio.endpoints.EndpointInfo;
import application.portfolio.endpoints.endpointClasses.user.userUtils.UserDeleteMethod;
import application.portfolio.endpoints.endpointClasses.user.userUtils.UserGetMethods;
import application.portfolio.endpoints.endpointClasses.user.userUtils.UserPostMethods;
import application.portfolio.objects.model.Person.PersonResponse;
import application.portfolio.utils.ParamsSplitter;
import application.portfolio.utils.ResponseHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import static java.net.HttpURLConnection.*;

@EndpointInfo(path = "/user")
public class UserEndpoint implements EndpointHandler, HttpHandler {
    @Override
    public HttpHandler endpoint() {
        return this;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            Map<String, String> params = ParamsSplitter.getParams(exchange.getRequestURI());
            if (params == null) {
                ResponseHandler.handleError(exchange, "Bad Data", HTTP_BAD_REQUEST);
                return;
            }

            if (params.containsKey("id")) {
                handleSingleUserRequest(exchange, params.get("id"));
            } else if (params.containsKey("offset") && params.containsKey("limit")) {
                String limit = params.get("limit");
                String offset = params.get("offset");
                handleMultipleUsersRequest(exchange, limit, offset);
            }
            throw new IOException();
        } catch (IOException e) {
            ResponseHandler.handleError(exchange, "Unknown Error", HTTP_INTERNAL_ERROR);
        }
    }

    private void handleSingleUserRequest(HttpExchange exchange, String id) {

        UUID userId;
        try {
            userId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            ResponseHandler.handleError(exchange, "Unknown Error", HTTP_INTERNAL_ERROR);
            return;
        }

        if ("GET".equals(exchange.getRequestMethod())) {

            PersonResponse personResponse = UserGetMethods.getPersonFromDatabase(userId);
            Map.Entry<Integer, JsonNode> responseNode = PersonResponse.toPersonJsonResponse(personResponse);

            ResponseHandler.sendResponse(exchange, responseNode);
        } else if ("POST".equals(exchange.getRequestMethod())) {

            byte[] data;
            try {
                data = exchange.getRequestBody().readAllBytes();
            } catch (IOException e) {
                ResponseHandler.handleError(exchange, "Unknown Error", HTTP_FORBIDDEN);
                return;
            }

            PersonResponse personResponse = UserPostMethods.modifyPerson(userId, data);
            Map.Entry<Integer, JsonNode> entry = PersonResponse.toPersonJsonResponse(personResponse);
            ResponseHandler.sendResponse(exchange, entry);
        } else if ("DELETE".equals(exchange.getRequestMethod())) {

            PersonResponse personResponse = UserDeleteMethod.deletePerson(userId);
            Map.Entry<Integer, JsonNode> entry = PersonResponse.toPersonJsonResponse(personResponse);
            ResponseHandler.sendResponse(exchange, entry);
        } else {
            ResponseHandler.handleError(exchange, "Bad Data", HTTP_FORBIDDEN);
        }
    }


    private void handleMultipleUsersRequest(HttpExchange exchange, String limit, String offset) {

        int iLimit, iOffset;
        try {
            iLimit = Integer.parseInt(limit);
            iOffset = Integer.parseInt(offset);
        } catch (NumberFormatException e) {
            ResponseHandler.handleError(exchange, "Bad Data", HTTP_FORBIDDEN);
            return;
        }

        if (iOffset < 0 || iLimit <= 0) {
            ResponseHandler.handleError(exchange, "Params out or range", HTTP_FORBIDDEN);
            return;
        }

        if (iLimit > 50) {
            iLimit = 10;
        }

        PersonResponse personResponse = UserGetMethods.getPersonsFromDatabase(iOffset, iLimit);
        Map.Entry<Integer, JsonNode> entry = PersonResponse.toPersonJsonResponse(personResponse);

        ResponseHandler.sendResponse(exchange, entry);
    }
}
