package application.portfolio.endpoints.endpointClasses.tasks;

import application.portfolio.endpoints.EndpointHandler;
import application.portfolio.endpoints.EndpointInfo;
import application.portfolio.endpoints.endpointClasses.tasks.taskUtils.TaskDeleteMethods;
import application.portfolio.endpoints.endpointClasses.tasks.taskUtils.TaskGetMethods;
import application.portfolio.endpoints.endpointClasses.tasks.taskUtils.TaskPostMethods;
import application.portfolio.endpoints.endpointClasses.tasks.taskUtils.TaskPutMethods;
import application.portfolio.utils.DataParser;
import application.portfolio.utils.ResponseHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;


import java.util.Map;

import static java.net.HttpURLConnection.*;

@EndpointInfo(path = "/tasks")
public class TaskEndpoint implements EndpointHandler, HttpHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public HttpHandler endpoint() {
        return this;
    }

    @Override
    public void handle(HttpExchange exchange) {

        String method = exchange.getRequestMethod();
        Map<String, String> paramsMap = DataParser.getParams(exchange.getRequestURI());
        try (exchange) {

            Map.Entry<Integer, JsonNode> responseEntry;
            if ("POST".equals(method)) {
                responseEntry = TaskPostMethods.handlePost(exchange);
            } else if ("PUT".equals(method)) {
                responseEntry = TaskPutMethods.handlePut(exchange);
            } else if (paramsMap != null) {
                responseEntry = switch (method) {
                    case "GET" -> TaskGetMethods.handleGet(paramsMap);
                    case "DELETE" -> TaskDeleteMethods.handleDelete(paramsMap);
                    default -> {
                        ObjectNode node = objectMapper.createObjectNode();
                        node.put("response", "Bad Method");
                        yield Map.entry(HTTP_BAD_METHOD, node);
                    }
                };
            } else {
                ResponseHandler.handleError(exchange, "Invalid Params", HTTP_BAD_REQUEST);
                return;
            }
            ResponseHandler.sendResponse(exchange, responseEntry);
        } catch (Exception e) {
            ResponseHandler.handleError(exchange, "Unknown Error", HTTP_INTERNAL_ERROR);
        }
    }
}
