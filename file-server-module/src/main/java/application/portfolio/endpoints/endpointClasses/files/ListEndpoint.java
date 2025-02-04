package application.portfolio.endpoints.endpointClasses.files;

import application.portfolio.endpoints.EndpointHandler;
import application.portfolio.endpoints.EndpointInfo;
import application.portfolio.endpoints.endpointClasses.files.FileUtils.BaseUtils;
import application.portfolio.endpoints.endpointClasses.files.FileUtils.LoadFiles;
import application.portfolio.endpoints.endpointClasses.files.FileUtils.ValidationResult;
import application.portfolio.utils.DataParser;
import application.portfolio.utils.ResponseHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.util.Map;

import static java.net.HttpURLConnection.*;

@EndpointInfo(path = "/store/list")
public class ListEndpoint implements EndpointHandler, HttpHandler {

    private final String[] MAP_PARAMS = {"userId", "path"};

    @Override
    public HttpHandler endpoint() {
        return this;
    }

    @Override
    public void handle(HttpExchange exchange) {
        try (exchange) {
            if (!"GET".equals(exchange.getRequestMethod())) {
                ResponseHandler.handleError(exchange, "Bad Method", HTTP_FORBIDDEN);
            } else {
                handleGet(exchange);
            }
        } catch (Exception e) {
            ResponseHandler.handleError(exchange, "Unknown Error", HTTP_INTERNAL_ERROR);
        }
    }

    private void handleGet(HttpExchange exchange) {

        Map<String, String> map = DataParser.getParams(exchange.getRequestURI());
        if (!DataParser.validateParams(map, MAP_PARAMS)) {
            ResponseHandler.handleError(exchange, "Bad Params", HTTP_BAD_REQUEST);
            return;
        }

        ValidationResult validationResult = BaseUtils.validateAndResolvePath(map);
        if (validationResult.isNotValid()) {
            int statusCode = validationResult.getStatusCode();
            JsonNode node = validationResult.getNode();
            ResponseHandler.sendResponse(exchange, Map.entry(statusCode, node));
            return;
        }

        Map.Entry<Integer, JsonNode> responseEntry = LoadFiles.loadFilesView(validationResult);
        ResponseHandler.sendResponse(exchange, responseEntry);
    }
}
