package application.portfolio.utils;


import java.net.URI;
import java.util.*;

public class DataParser {

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

    public static boolean validateParams(Map<String, String> paramsMap, String... params) {
        if (paramsMap != null) {
            return paramsMap.keySet().containsAll(Arrays.asList(params)) && paramsMap.size() == params.length;
        }
        return false;
    }
}
