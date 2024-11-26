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
        if (map.containsKey("id")) {
            sb.append("id=").append(map.get("id"));
            sj.add(sb.toString());
            return sj.toString();
        }

        for (Map.Entry<String, String> m : map.entrySet()) {
            if (m.getKey().equals("limit") || m.getKey().equals("offset")) {
                sb.append(m.getKey())
                        .append("=")
                        .append(m.getValue());

                sj.add(sb.toString());
                sb.setLength(0);
            } else {
                return null;
            }
        }
        return sj.toString();
    }
}
