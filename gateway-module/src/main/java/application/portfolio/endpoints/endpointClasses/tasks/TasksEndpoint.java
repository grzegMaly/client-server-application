package application.portfolio.endpoints.endpointClasses.tasks;

import application.portfolio.endpoints.EndpointHandler;
import application.portfolio.endpoints.EndpointInfo;
import application.portfolio.endpoints.endpointClasses.tasks.taskUtils.TaskDeleteMethods;
import application.portfolio.endpoints.endpointClasses.tasks.taskUtils.TaskGetMethods;
import application.portfolio.endpoints.endpointClasses.tasks.taskUtils.TaskPostMethods;
import application.portfolio.endpoints.endpointClasses.tasks.taskUtils.TaskPutMethods;
import application.portfolio.requestResponse.ResponseHandler;
import application.portfolio.utils.DataParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;

@EndpointInfo(path = "/tasks")
public class TasksEndpoint implements EndpointHandler, HttpHandler {

    @Override
    public HttpHandler endpoint() {
        return this;
    }

    @Override
    public void handle(HttpExchange exchange) {

        String method = exchange.getRequestMethod();
        try (exchange) {

            if ("POST".equals(method)) {
                TaskPostMethods.handlePost(exchange);
            } else if ("PUT".equals(method)) {
                TaskPutMethods.handlePut(exchange);
            } else {
                Map<String, String> paramsMap = DataParser.getParams(exchange.getRequestURI());
                if (paramsMap == null) {
                    ResponseHandler.handleError(exchange, "Unknown Error", HTTP_INTERNAL_ERROR);
                    return;
                }
                switch (method) {
                    case "GET" -> TaskGetMethods.handleGet(exchange, paramsMap);
                    case "DELETE" -> TaskDeleteMethods.handleDelete(exchange, paramsMap);
                    default -> ResponseHandler.handleError(exchange, "Unknown Error", HTTP_INTERNAL_ERROR);
                }
            }
        } catch (Exception e) {
            ResponseHandler.handleError(exchange, "Unknown Error", HTTP_INTERNAL_ERROR);
        }
    }
}