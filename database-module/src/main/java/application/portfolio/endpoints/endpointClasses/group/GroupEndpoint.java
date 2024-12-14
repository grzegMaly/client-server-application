package application.portfolio.endpoints.endpointClasses.group;

import application.portfolio.endpoints.EndpointHandler;
import application.portfolio.endpoints.EndpointInfo;
import application.portfolio.endpoints.endpointClasses.group.groupUtils.GroupDeleteMethod;
import application.portfolio.endpoints.endpointClasses.group.groupUtils.GroupGetMethod;
import application.portfolio.clientServer.response.GroupResponse;
import application.portfolio.endpoints.endpointClasses.group.groupUtils.GroupPostMethods;
import application.portfolio.utils.DataParser;
import application.portfolio.utils.ResponseHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Map;

import static java.net.HttpURLConnection.*;

@EndpointInfo(path = "/group")
public class GroupEndpoint implements EndpointHandler, HttpHandler {
    @Override
    public HttpHandler endpoint() {
        return this;
    }

    @Override
    public void handle(HttpExchange exchange) {
        try (exchange) {

            GroupResponse groupResponse = null;
            if ("GET".equals(exchange.getRequestMethod())) {
                groupResponse = handleGet(exchange);
            } else if ("POST".equals(exchange.getRequestMethod())) {
                groupResponse = handlePost(exchange);
            } else if ("DELETE".equals(exchange.getRequestMethod())) {
                groupResponse = handleDelete(exchange);
            } else {
                groupResponse = new GroupResponse("Bad Gateway", HTTP_BAD_GATEWAY);
            }

            Map.Entry<Integer, JsonNode> entry = groupResponse.toJsonResponse();
            ResponseHandler.sendResponse(exchange, entry);
        } catch (IOException e) {
            ResponseHandler.handleError(exchange, "Unknown Error", HTTP_INTERNAL_ERROR);
        }
    }

    private GroupResponse handleGet(HttpExchange exchange) {

        Map<String, String> paramsMap = DataParser.getParams(exchange.getRequestURI());
        if (paramsMap == null) {
            return new GroupResponse("Bad Data", HTTP_BAD_REQUEST);
        }

        GroupResponse groupResponse = null;
        if (paramsMap.containsKey("id")) {
            groupResponse = GroupGetMethod.getGroupFromDatabase(paramsMap.get("id"));
        } else if (paramsMap.containsKey("limit") && paramsMap.containsKey("offset")) {
            String offset = paramsMap.get("offset");
            String limit = paramsMap.get("limit");
            groupResponse = GroupGetMethod.getGroupFromDatabase(offset, limit);
        } else {
            groupResponse = new GroupResponse("Forbidden", HTTP_FORBIDDEN);
        }
        return groupResponse;
    }

    private GroupResponse handlePost(HttpExchange exchange) throws IOException {

        Map<String, String> paramsMap = DataParser.getParams(exchange.getRequestURI());

        if (paramsMap == null || !paramsMap.containsKey("option")) {
            return new GroupResponse("Bad Data", HTTP_FORBIDDEN);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        byte[] data = exchange.getRequestBody().readAllBytes();
        JsonNode node = objectMapper.readTree(data);
        GroupResponse groupResponse;

        String option = paramsMap.get("option");
        if (option.equals("modify")) {
            groupResponse = GroupPostMethods.modifyGroup(node);
        } else if (option.equals("add")) {
            groupResponse = GroupPostMethods.addGroup(node);
        } else {
            groupResponse = new GroupResponse("Bad Request", HTTP_BAD_REQUEST);
        }
        return groupResponse;
    }

    private GroupResponse handleDelete(HttpExchange exchange) {

        Map<String, String> paramsMap = DataParser.getParams(exchange.getRequestURI());
        if (paramsMap == null || !paramsMap.containsKey("id")) {
            return new GroupResponse("Bad Data", HTTP_FORBIDDEN);
        }

        GroupResponse groupResponse;
        String id = paramsMap.get("id");
        groupResponse = GroupDeleteMethod.deleteGroup(id);
        return groupResponse;
    }
}

