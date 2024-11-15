package application.portfolio.endpoints.endpointClasses.user;

import application.portfolio.endpoints.EndpointHandler;
import application.portfolio.endpoints.EndpointInfo;
import application.portfolio.endpoints.endpointClasses.user.userUtils.UserMethods;
import application.portfolio.utils.ResponseHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;

@EndpointInfo(path = "/user")
public class UserEndpoint implements EndpointHandler, HttpHandler {
    @Override
    public HttpHandler endpoint() {
        return this;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            Map<String, String> params = getParams(exchange.getRequestURI());
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
            Map.Entry<Integer, JsonNode> entry = UserMethods.getPersonFromDatabase(userId);
            ResponseHandler.sendResponse(exchange, entry);
        }
    }

    private void handleMultipleUsersRequest(HttpExchange exchange, String limit, String offset) {

    }

    private static Map<String, String> getParams(URI uri) {

        String params = uri.getQuery();
        Map<String, String> paramsMap;

        if (params == null) {
            return null;
        }

        String[] splitParams = params.split("&");
        paramsMap = new LinkedHashMap<>();

        for (String s : splitParams) {
            String[] keyVal = s.split("=", 2);
            if (keyVal.length == 2) {
                paramsMap.put(keyVal[0], keyVal[1]);
            }
        }
        return paramsMap;
    }
}
