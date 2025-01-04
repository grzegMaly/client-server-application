package application.portfolio.endpoints.endpointClasses.user;

import application.portfolio.endpoints.EndpointHandler;
import application.portfolio.endpoints.EndpointInfo;
import application.portfolio.endpoints.endpointClasses.user.userUtils.UserDeleteMethod;
import application.portfolio.endpoints.endpointClasses.user.userUtils.UserGetMethods;
import application.portfolio.endpoints.endpointClasses.user.userUtils.UserPostMethods;
import application.portfolio.clientServer.response.PersonResponse;
import application.portfolio.utils.DataParser;
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
    public void handle(HttpExchange exchange) {
        try (exchange) {
            Map<String, String> params = DataParser.getParams(exchange.getRequestURI());
            PersonResponse personResponse;
            String method = exchange.getRequestMethod();
            if (params == null) {
                if ("POST".equals(method)) {
                    personResponse = handlePost(exchange);
                } else {
                    ResponseHandler.handleError(exchange, "Bad Data", HTTP_BAD_REQUEST);
                    return;
                }
            } else {
                switch (method) {
                    case "GET" -> personResponse = handleGet(params);
                    case "DELETE" -> personResponse = handleDelete(params);
                    default -> {
                        ResponseHandler.handleError(exchange, "Bad Data", HTTP_BAD_REQUEST);
                        return;
                    }
                }
            }

            Map.Entry<Integer, JsonNode> entry = personResponse.toJsonResponse();
            ResponseHandler.sendResponse(exchange, entry);
        } catch (Exception e) {
            ResponseHandler.handleError(exchange, "Unknown Error", HTTP_INTERNAL_ERROR);
        }
    }

    private PersonResponse handleGet(Map<String, String> params) {

        if (DataParser.validateParams(params, "id")) {
            return handleGetById(params.get("id"));
        }

        if (DataParser.validateParams(params, "offset", "limit")) {
            return handleGetWithPagination(params.get("offset"), params.get("limit"));
        }

        return new PersonResponse("Bad Data", HTTP_FORBIDDEN);
    }

    private PersonResponse handleGetById(String id) {
        UUID userId = DataParser.parseId(id);
        if (userId == null) {
            return new PersonResponse("Bad Data", HTTP_FORBIDDEN);
        }
        return UserGetMethods.getPersonFromDatabase(userId);
    }

    private PersonResponse handleGetWithPagination(String offset, String limit) {
        int iLimit, iOffset;
        try {
            iLimit = Integer.parseInt(limit);
            iOffset = Integer.parseInt(offset);
        } catch (NumberFormatException e) {
            return new PersonResponse("Bad Data", HTTP_FORBIDDEN);
        }

        if (iOffset < 0 || iLimit <= 0) {
            return new PersonResponse("Bad Data", HTTP_FORBIDDEN);
        }

        if (iLimit > 50) {
            iLimit = 10;
        }

        return UserGetMethods.getPersonsFromDatabase(iOffset, iLimit);
    }

    private PersonResponse handlePost(HttpExchange exchange) {

        byte[] data;
        try {
            data = exchange.getRequestBody().readAllBytes();
        } catch (IOException e) {
            return new PersonResponse("Unknown Error", HTTP_FORBIDDEN);
        }
        return UserPostMethods.modifyPerson(data);
    }

    private PersonResponse handleDelete(Map<String, String> params) {

        if (!DataParser.validateParams(params, "id")) {
            return new PersonResponse("Bad Data", HTTP_FORBIDDEN);
        }

        String id = params.get("id");
        UUID userId = DataParser.parseId(id);
        if (userId == null) {
            return new PersonResponse("Bad Data", HTTP_FORBIDDEN);
        }
        return UserDeleteMethod.deletePerson(userId);
    }
}
