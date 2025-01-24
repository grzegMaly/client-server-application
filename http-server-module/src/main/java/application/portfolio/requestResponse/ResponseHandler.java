package application.portfolio.requestResponse;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;

public class ResponseHandler {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final List<String> RESTRICTED_HEADERS = List.of(
            "CONNECTION", "HOST", "UPGRADE", "CONTENT-LENGTH", "EXPECT", "TE", "TRANSFER-ENCODING", "TRAILER"
    );

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

    public static <T> void handleReceivedResponse(HttpExchange exchange, HttpResponse<T> response) {

        if (response == null) {
            handleError(exchange, "Unknown Error", HTTP_INTERNAL_ERROR);
        } else {
            if (response.body() instanceof byte[] rByte) {
                sendResponse(exchange, rByte, response.statusCode());
            } else if (response.body() instanceof InputStream is) {
                handleStreamResponse(exchange, is, response.statusCode());
            }
        }
    }

    private static void handleStreamResponse(HttpExchange exchange, InputStream inputStream, int statusCode) {

        try (inputStream; OutputStream os = exchange.getResponseBody()) {
            inputStream.transferTo(os);
            exchange.sendResponseHeaders(statusCode, 0);
        } catch (IOException e) {
            handleError(exchange, "Unknown Error", HTTP_INTERNAL_ERROR);
        }
    }

    public static void copyHeaders(HttpExchange exchange, HttpRequest.Builder requestBuilder) {
        exchange.getRequestHeaders().forEach((key, value) -> {
            if (!RESTRICTED_HEADERS.contains(key.toUpperCase())) {
                String headers = String.join(", ", value);
                requestBuilder.header(key, headers);
            }
        });
    }
}
