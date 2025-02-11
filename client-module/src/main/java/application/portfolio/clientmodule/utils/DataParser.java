package application.portfolio.clientmodule.utils;

import com.fasterxml.jackson.databind.JsonNode;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;

public class DataParser {
    public synchronized static String paramsString(Map<String, String> map) {
        StringJoiner sj = new StringJoiner("&", "?", "");

        for (Map.Entry<String, String> m : map.entrySet()) {
            String encodedValue = URLEncoder.encode(m.getValue(), StandardCharsets.UTF_8); // **Encodowanie TYLKO wartości**
            sj.add(m.getKey() + "=" + encodedValue);
        }
        return sj.toString();
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

    public static boolean validateNode(JsonNode node, String[] keys) {
        for (String key : keys) {
            if (!node.hasNonNull(key)) {
                return false;
            }
        }
        return true;
    }
}
