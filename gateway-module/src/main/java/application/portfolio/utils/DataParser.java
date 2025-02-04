package application.portfolio.utils;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class DataParser {

    public synchronized static Map<String, String> getParams(URI uri) {

        String params = uri.getQuery();
        return getParams(params);
    }

    public synchronized static Map<String, String> getParams(String params) {

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
        StringJoiner sj = new StringJoiner("&", "?", "");

        for (Map.Entry<String, String> m : map.entrySet()) {
            String encodedValue = URLEncoder.encode(m.getValue(), StandardCharsets.UTF_8);
            sj.add(m.getKey() + "=" + encodedValue);
        }
        return sj.toString();
    }

    public static boolean validateParams(Map<String, String> paramsMap, String... params) {

        if (paramsMap != null) {
            return paramsMap.keySet().containsAll(Arrays.asList(params)) && paramsMap.size() == params.length;
        }
        return false;
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
