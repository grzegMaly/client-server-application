package application.portfolio.utils;

import com.fasterxml.jackson.databind.JsonNode;

public class DataParser {

    public static String parseElements(JsonNode node, String key) {
        JsonNode valueNode = node.get(key);
        if (valueNode == null) {
            throw new IllegalArgumentException();
        } else if (valueNode.asText().isBlank()) {
            throw new IllegalArgumentException(key + " is blank");
        }
        return valueNode.asText();
    }
}
