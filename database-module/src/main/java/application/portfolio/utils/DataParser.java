package application.portfolio.utils;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;


public class DataParser {

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
}
