package application.portfolio.endpoints.endpointClasses.files;

import application.portfolio.endpoints.EndpointHandler;
import application.portfolio.endpoints.EndpointInfo;
import application.portfolio.endpoints.endpointClasses.files.FileUtils.*;
import application.portfolio.utils.DataParser;
import application.portfolio.utils.ResponseHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;


import java.nio.file.Path;
import java.util.Map;

import static java.net.HttpURLConnection.*;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;

@EndpointInfo(path = "/store/resource")
public class ResourceEndpoint implements HttpHandler, EndpointHandler {

    private static final String[] RESOURCE_PARAMS = {"userId", "path"};

    @Override
    public HttpHandler endpoint() {
        return this;
    }

    @Override
    public void handle(HttpExchange exchange) {

        String method = exchange.getRequestMethod();
        Map<String, String> map = DataParser.getParams(exchange.getRequestURI());
        if (!DataParser.validateParams(map, RESOURCE_PARAMS)) {
            ResponseHandler.handleError(exchange, "Bad Params", HTTP_FORBIDDEN);
            return;
        }

        try (exchange) {
            if ("GET".equals(method)) {
                handleGet(exchange, map);
            } else if ("POST".equals(method)) {
                 handlePost(exchange, map);
            } else if ("DELETE".equals(method)) {
                handleDelete(exchange, map);
            } else {
                ResponseHandler.handleError(exchange, "Bad Method", HTTP_BAD_REQUEST);
            }
        } catch (Exception e) {
            ResponseHandler.handleError(exchange, "Unknown Error", HTTP_INTERNAL_ERROR);
        }
    }

    private void handleGet(HttpExchange exchange, Map<String, String> paramsMap) {

        ValidationResult validationResult = BaseUtils.validateAndResolvePath(paramsMap);
        if (validationResult.isNotValid()) {
            int statusCode = validationResult.getStatusCode();
            JsonNode node = validationResult.getNode();
            ResponseHandler.sendResponse(exchange, Map.entry(statusCode, node));
            return;
        }

        Path resourcePath = validationResult.getValidatedPath();
        ResourceGetMethods.handleDownload(exchange, resourcePath);
    }

    private void handlePost(HttpExchange exchange, Map<String, String> paramsMap) {

        String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
        if (contentType == null) {
            ResponseHandler.handleError(exchange, "Missing Content-Type in Headers", HTTP_BAD_REQUEST);
            return;
        }

        ValidationResult validationResult = BaseUtils.validateAndResolvePath(paramsMap);
        if (validationResult.isNotValid()) {
            int statusCode = validationResult.getStatusCode();
            JsonNode node = validationResult.getNode();
            ResponseHandler.sendResponse(exchange, Map.entry(statusCode, node));
            return;
        }

        Map.Entry<Integer, JsonNode> resultEntry;
        Path destinationPath = validationResult.getValidatedPath();
        switch (contentType) {
            case "application/octet-stream" ->
                    resultEntry = ResourcePostMethods.handleSingleFileUpload(exchange, destinationPath);
            case "application/zip" ->
                    resultEntry = ResourcePostMethods.handleZipStreamUpload(exchange, destinationPath);
            default -> {
                ResponseHandler.handleError(exchange, "Unknown Content-Type", HTTP_BAD_REQUEST);
                return;
            }
        }
        ResponseHandler.sendResponse(exchange, resultEntry);
    }

    private void handleDelete(HttpExchange exchange, Map<String, String> paramsMap) {
        Map.Entry<Integer, JsonNode> resultEntry = ResourceDeleteMethods.handleDelete(paramsMap);
        ResponseHandler.sendResponse(exchange, resultEntry);
    }
}
