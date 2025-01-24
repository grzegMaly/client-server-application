package application.portfolio.endpoints.endpointClasses.files;

import application.portfolio.endpoints.EndpointHandler;
import application.portfolio.endpoints.EndpointInfo;
import application.portfolio.endpoints.endpointClasses.files.FileUtils.LoadFiles;
import application.portfolio.utils.DataParser;
import application.portfolio.utils.ResponseHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;

@EndpointInfo(path = "/store/list")
public class ListEndpoint implements EndpointHandler, HttpHandler {

    private final String[] MAP_PARAMS = {"userId", "path"};

    @Override
    public HttpHandler endpoint() {
        return this;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            if (!"GET".equals(exchange.getRequestMethod())) {
                ResponseHandler.handleError(exchange, "Bad Method", HTTP_FORBIDDEN);
            } else {
                handleGet(exchange);
            }
        }
    }

    private void handleGet(HttpExchange exchange) {

        Map<String, String> map = DataParser.getParams(exchange.getRequestURI());
        if (!DataParser.validateParams(map, MAP_PARAMS)) {
            ResponseHandler.handleError(exchange, "Bad Params", HTTP_BAD_REQUEST);
            return;
        }
        String id = map.get("userId");
        UUID uId = DataParser.parseId(id);
        if (uId == null) {
            ResponseHandler.handleError(exchange, "Invalid Params", HTTP_BAD_REQUEST);
            return;
        }

        String path = map.get("path");

        if (!path.contains("/")) {
            ResponseHandler.handleError(exchange, "Invalid Params", HTTP_FORBIDDEN);
            return;
        }

        String[] pathElements = path.substring(1).split("/");

        Map.Entry<Integer, JsonNode> responseEntry = LoadFiles.loadFilesView(uId, pathElements);
        ResponseHandler.sendResponse(exchange, responseEntry);
    }
}
