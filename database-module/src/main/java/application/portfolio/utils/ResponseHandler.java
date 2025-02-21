package application.portfolio.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;

public class ResponseHandler {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String responseKey = "response";

    static {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static void handleError(HttpExchange exchange, String message, int statusCode) {

        ObjectNode responseNode = objectMapper.createObjectNode();
        responseNode.put(responseKey, message);

        Map.Entry<Integer, JsonNode> responseData = new AbstractMap.SimpleEntry<>(statusCode, responseNode);
        sendResponse(exchange, responseData);
    }

    public static void sendResponse(HttpExchange exchange, Map.Entry<Integer, JsonNode> responseData) {

        int statusCode = responseData.getKey();
        JsonNode responseNode = responseData.getValue();

        byte[] data;
        try {
            data = objectMapper.writeValueAsBytes(responseNode);
            exchange.sendResponseHeaders(statusCode, data.length);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.getResponseBody().write(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
