package application.portfolio.endpoints.endpointClasses.groupUser;

import application.portfolio.clientServer.response.GroupResponse;
import application.portfolio.clientServer.response.PersonResponse;
import application.portfolio.clientServer.response.Response;
import application.portfolio.endpoints.EndpointHandler;
import application.portfolio.endpoints.EndpointInfo;
import application.portfolio.endpoints.endpointClasses.group.groupUtils.GroupDeleteMethod;
import application.portfolio.endpoints.endpointClasses.group.groupUtils.GroupGetMethod;
import application.portfolio.endpoints.endpointClasses.group.groupUtils.GroupPostMethods;
import application.portfolio.endpoints.endpointClasses.user.userUtils.UserGetMethods;
import application.portfolio.utils.DataParser;
import application.portfolio.utils.ResponseHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;

@EndpointInfo(path = "/group/user")
public class GroupUsersEndpoint implements EndpointHandler, HttpHandler {
    @Override
    public HttpHandler endpoint() {
        return this;
    }

    @Override
    public void handle(HttpExchange exchange) {
        try (exchange) {
            URI uri = exchange.getRequestURI();
            Map<String, String> params = DataParser.getParams(uri);
            String method = exchange.getRequestMethod();
            Map.Entry<Integer, JsonNode> entry;

            if (params == null) {
                if ("POST".equals(method)) {
                    entry = handlePost(exchange);
                } else {
                    entry = new Response<>("Bad Data", HTTP_BAD_REQUEST).toJsonResponse();
                }
            } else {
                switch (method) {
                    case "GET" -> {
                        if (params.containsKey("groupId")) {
                            entry = handleGroupGet(params);
                        } else if (params.containsKey("userId")) {
                            entry = handleUserGet(params);
                        } else {
                            entry = new Response<>("Bad Data", HTTP_BAD_REQUEST).toJsonResponse();
                        }
                    }
                    case "DELETE" -> {
                         if (params.containsKey("userId") && params.containsKey("groupId")) {
                            entry = handleUserDelete(params);
                        } else {
                            entry = new Response<>("Bad Data", HTTP_BAD_REQUEST).toJsonResponse();
                        }
                    }
                    default -> {
                        ResponseHandler.handleError(exchange, "Bad Data", HTTP_BAD_REQUEST);
                        return;
                    }
                }
            }
            ResponseHandler.sendResponse(exchange, entry);
        } catch (IOException e) {
            ResponseHandler.handleError(exchange, "Unknown Error", HTTP_INTERNAL_ERROR);
        }
    }

    private Map.Entry<Integer, JsonNode> handleGroupGet(Map<String, String> params) {

        PersonResponse personResponse;
        String id = params.get("groupId");
        if (params.containsKey("offset") && params.containsKey("limit") && params.size() == 3) {
            String offset = params.get("offset");
            String limit = params.get("limit");

            personResponse = GroupGetMethod.getGroupMembers(id, offset, limit);
        } else {
            personResponse = new PersonResponse("Invalid Parameters", HTTP_BAD_REQUEST);
        }
        return personResponse.toJsonResponse();
    }

    private Map.Entry<Integer, JsonNode> handleUserGet(Map<String, String> params) {

        String userId = params.get("userId");
        Map.Entry<Integer, JsonNode> entry;
        if (params.size() == 1) {
            entry = GroupGetMethod.getUserGroups(userId).toJsonResponse();
        } else if (params.containsKey("friends") && params.size() == 2) {
            String friendsVal = params.get("friends");
            boolean val = Boolean.parseBoolean(friendsVal);
            if (val) {
                entry = UserGetMethods.getUsersFriends(userId).toJsonResponse();
            } else {
                entry = new Response<>("Bad Data", HTTP_BAD_REQUEST).toJsonResponse();
            }
        } else {
            entry = new Response<>("Bad Data", HTTP_BAD_REQUEST).toJsonResponse();
        }
        return entry;
    }

    private Map.Entry<Integer, JsonNode> handlePost(HttpExchange exchange) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        byte[] data = exchange.getRequestBody().readAllBytes();
        JsonNode node = objectMapper.readTree(data);

        GroupResponse groupResponse = GroupPostMethods.addUserToGroup(node);
        return groupResponse.toJsonResponse();
    }

    private Map.Entry<Integer, JsonNode> handleUserDelete(Map<String, String> params) {

        Map.Entry<Integer, JsonNode> entry;
        if (params.size() != 2) {
            entry = new Response<>("Invalid Parameters", HTTP_BAD_REQUEST).toJsonResponse();
        } else {
            String userId = params.get("userId");
            String groupId = params.get("groupId");
            entry = GroupDeleteMethod.deleteUserFromGroup(userId, groupId).toJsonResponse();
        }
        return entry;
    }
}