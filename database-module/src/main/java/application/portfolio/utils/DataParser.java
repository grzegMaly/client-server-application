package application.portfolio.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class DataParser {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static JsonNode convertToNode(HttpExchange exchange) {

        try {
            return objectMapper.readTree(exchange.getRequestBody());
        } catch (IOException e) {
            return null;
        }
    }

    public static String parseElement(JsonNode node, String key) throws IllegalArgumentException {
        JsonNode valueNode = node.get(key);
        if (valueNode == null) {
            throw new IllegalArgumentException();
        } else if (valueNode.asText().isBlank()) {
            throw new IllegalArgumentException(key + " is blank");
        }
        return valueNode.asText();
    }

    public static String[] parseElements(JsonNode node, String... keys) throws IllegalArgumentException {

        String[] responseData = new String[keys.length];
        int count = 0;

        for (String key : keys) {
            JsonNode valueNode = node.get(key);
            if (valueNode == null) {
                throw new IllegalArgumentException();
            } else if (valueNode.asText().isBlank()) {
                throw new IllegalArgumentException(key + " is blank");
            }
            responseData[count++] = valueNode.asText();
        }
        return responseData;
    }

    public static String[] getNodeKeys(JsonNode node) {
        List<String> keys = new ArrayList<>();
        node.fieldNames().forEachRemaining(keys::add);
        return keys.toArray(String[]::new);
    }

    public static UUID parseId(String id) {

        UUID userId = null;
        try {
            userId = UUID.fromString(id);
        } catch (IllegalArgumentException ignore) {
            //Nothing
        }
        return userId;
    }

    public static Map<String, String> getParams(URI uri) {

        String params = uri.getQuery();
        Map<String, String> paramsMap;

        if (params == null) {
            return null;
        }

        String[] splitParams = params.split("&");
        paramsMap = new LinkedHashMap<>();

        for (String s : splitParams) {
            String[] keyVal = s.split("=", 2);
            if (keyVal.length == 2) {
                paramsMap.put(keyVal[0], keyVal[1]);
            }
        }
        return paramsMap;
    }

    public static boolean isNullOrEmpty(String value) {
        return value == null || value.isEmpty();
    }

    public static boolean validateParams(Map<String, String> paramsMap, String... params) {

        if (paramsMap != null) {
            return paramsMap.keySet().containsAll(Arrays.asList(params)) && paramsMap.size() == params.length;
        }
        return false;
    }

    public static boolean validateNode(JsonNode node, String[] keys) {
        for (String key : keys) {
            if (!node.hasNonNull(key)) {
                return false;
            }
        }
        return true;
    }

    public synchronized static String paramsString(Map<String, String> map) {
        StringJoiner sj = new StringJoiner("&", "?", "");

        for (Map.Entry<String, String> m : map.entrySet()) {
            String encodedValue = URLEncoder.encode(m.getValue(), StandardCharsets.UTF_8);
            sj.add(m.getKey() + "=" + encodedValue);
        }
        return sj.toString();
    }
}
