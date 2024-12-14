package application.portfolio.utils;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

public class DataParser {

    public synchronized static Map<String, String> getParams(URI uri) {

        String params = uri.getQuery();

        if (params == null) {
            return null;
        }

        String[] splitParams = params.split("&");
        Map<String, String> paramsMap = new LinkedHashMap<>();

        for (String s : splitParams) {
            String[] keyVal = s.split("=", 2);
            if (keyVal.length == 2) {
                paramsMap.put(keyVal[0], keyVal[1]);
            }
        }
        return paramsMap;
    }

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

    public static Map<String, String> handleMap(Map<String, String> paramsMap) {
        if (paramsMap == null) {
            return Map.of("limit", "10", "offset", "0");
        }
        return paramsMap;
    }
}
