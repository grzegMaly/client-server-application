package application.portfolio.clientmodule.utils;

import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;

public class DataParser {
    public synchronized static String paramsString(Map<String, String> map) {

        StringBuilder sb = new StringBuilder();
        StringJoiner sj = new StringJoiner("&", "?", "");

        for (Map.Entry<String, String> m : map.entrySet()) {
            sb.append(m.getKey())
                    .append("=")
                    .append(m.getValue());

            sj.add(sb.toString());
            sb.setLength(0);
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
}
