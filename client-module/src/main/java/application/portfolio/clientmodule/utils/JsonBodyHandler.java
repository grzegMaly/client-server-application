package application.portfolio.clientmodule.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.http.HttpResponse;

public class JsonBodyHandler implements HttpResponse.BodyHandler<JsonNode> {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static JsonBodyHandler handler;

    @Override
    public HttpResponse.BodySubscriber<JsonNode> apply(HttpResponse.ResponseInfo responseInfo) {
        return HttpResponse.BodySubscribers.mapping(HttpResponse.BodySubscribers.ofByteArray(), bytes -> {
            try {
                return objectMapper.readTree(bytes);
            } catch (IOException e) {
                throw new RuntimeException("Failed to parse JSON, ", e);
            }
        });
    }

    public static JsonBodyHandler getJsonHandler() {
        if (handler == null) {
            handler = new JsonBodyHandler();
        }
        return handler;
    }
}
