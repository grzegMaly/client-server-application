package application.portfolio.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.http.HttpResponse;

public class ResponseHandler {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static void handleError(HttpExchange exchange, String message, int statusCode) {

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("response", message);
        byte[] data = null;
        try {
            data = objectMapper.writeValueAsBytes(objectNode);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        sendResponse(exchange, data, statusCode);
    }

    public static void sendResponse(HttpResponse<byte[]> response, HttpExchange exchange) {
        if (response != null) {
            int statusCode = response.statusCode();
            byte[] responseData = response.body();

            response.headers().map().forEach((key, value) -> {
                value.forEach(v -> exchange.getResponseHeaders().add(key, v));
            });
            sendResponse(exchange, responseData, statusCode);
        }
    }

    public static void sendResponse(HttpExchange exchange, byte[] responseData, int statusCode) {
        try {
            exchange.sendResponseHeaders(statusCode, responseData.length);
            exchange.getResponseBody().write(responseData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
