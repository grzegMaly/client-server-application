package application.portfolio.endpoints.endpointClasses.group;

import application.portfolio.endpoints.EndpointHandler;
import application.portfolio.endpoints.EndpointInfo;
import application.portfolio.endpoints.endpointClasses.group.groupUtils.GroupDeleteMethod;
import application.portfolio.endpoints.endpointClasses.group.groupUtils.GroupGetMethod;
import application.portfolio.clientServer.response.GroupResponse;
import application.portfolio.endpoints.endpointClasses.group.groupUtils.GroupPostMethods;
import application.portfolio.endpoints.endpointClasses.group.groupUtils.GroupPutMethods;
import application.portfolio.utils.DataParser;
import application.portfolio.utils.ResponseHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

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

            String method = exchange.getRequestMethod();
            GroupResponse groupResponse = switch (method) {
                case "GET" -> handleGet(exchange);
                case "POST" -> {
                    JsonNode node = DataParser.convertToNode(exchange);
                    yield GroupPostMethods.addGroup(node);
                }
                case "PUT" -> {
                    JsonNode node = DataParser.convertToNode(exchange);
                    yield GroupPutMethods.modifyGroup(node);
                }
                case "DELETE" -> handleDelete(exchange);
                default -> new GroupResponse("Bad Gateway", HTTP_BAD_GATEWAY);
            };

            Map.Entry<Integer, JsonNode> entry = groupResponse.toJsonResponse();
            ResponseHandler.sendResponse(exchange, entry);
        } catch (Exception e) {
            ResponseHandler.handleError(exchange, "Unknown Error", HTTP_INTERNAL_ERROR);
        }
    }

    private GroupResponse handleGet(HttpExchange exchange) {

        Map<String, String> paramsMap = DataParser.getParams(exchange.getRequestURI());
        if (paramsMap == null) {
            return new GroupResponse("Bad Data", HTTP_BAD_REQUEST);
        }

        GroupResponse groupResponse;
        if (paramsMap.containsKey("id")) {
            groupResponse = GroupGetMethod.getGroupFromDatabase(paramsMap.get("id"));
        } else if (paramsMap.containsKey("limit") && paramsMap.containsKey("offset")) {
            String offset = paramsMap.get("offset");
            String limit = paramsMap.get("limit");
            groupResponse = GroupGetMethod.getGroupFromDatabase(offset, limit);
        } else if (paramsMap.containsKey("all")) {
            if (paramsMap.get("all").equalsIgnoreCase("true")) {
                groupResponse = GroupGetMethod.getAllGroupsFromDatabase();
            } else {
                groupResponse = new GroupResponse("Forbidden", HTTP_FORBIDDEN);
            }
        } else {
            groupResponse = new GroupResponse("Forbidden", HTTP_FORBIDDEN);
        }
        return groupResponse;
    }

    private GroupResponse handleDelete(HttpExchange exchange) {

        Map<String, String> paramsMap = DataParser.getParams(exchange.getRequestURI());
        if (paramsMap == null || !paramsMap.containsKey("id")) {
            return new GroupResponse("Bad Data", HTTP_FORBIDDEN);
        }

        String id = paramsMap.get("id");
        return GroupDeleteMethod.deleteGroup(id);
    }
}

