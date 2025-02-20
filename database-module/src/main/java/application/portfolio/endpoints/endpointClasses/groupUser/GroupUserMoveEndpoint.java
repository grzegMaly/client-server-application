package application.portfolio.endpoints.endpointClasses.groupUser;

import application.portfolio.clientServer.response.GroupResponse;
import application.portfolio.endpoints.EndpointHandler;
import application.portfolio.endpoints.EndpointInfo;
import application.portfolio.endpoints.endpointClasses.group.groupUtils.GroupPostMethods;
import application.portfolio.utils.DataParser;
import application.portfolio.utils.ResponseHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

import static java.net.HttpURLConnection.*;

@EndpointInfo(path = "/group/user/move")
public class GroupUserMoveEndpoint implements EndpointHandler, HttpHandler {

    private static final List<String> MOVE_PARAMS = List.of("fromGroup", "toGroup", "userId");

    @Override
    public HttpHandler endpoint() {
        return this;
    }

    @Override
    public void handle(HttpExchange exchange) {

        try (exchange) {

            String method = exchange.getRequestMethod();
            GroupResponse groupResponse;

            if (!"POST".equals(method)) {
                groupResponse = new GroupResponse("Bad Request", HTTP_BAD_REQUEST);
            } else {
                groupResponse = handlePostMove(exchange);
            }

            Map.Entry<Integer, JsonNode> entry = groupResponse.toJsonResponse();
            ResponseHandler.sendResponse(exchange, entry);
        } catch (IOException e) {
            ResponseHandler.handleError(exchange, "Unknown Error", HTTP_INTERNAL_ERROR);
        }
    }

    private GroupResponse handlePostMove(HttpExchange exchange) throws IOException {

        URI uri = exchange.getRequestURI();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> params = DataParser.getParams(uri);
        JsonNode node;

        if (params == null) {
            node = DataParser.convertToNode(exchange);
        } else if (params.size() == 3 && params.keySet().containsAll(MOVE_PARAMS)) {

            ObjectNode objectNode = objectMapper.createObjectNode();
            params.forEach(objectNode::put);
            node = objectNode;
        } else {
            return new GroupResponse("Bad Data", HTTP_FORBIDDEN);
        }
        return GroupPostMethods.moveUserToGroup(node);
    }
}
