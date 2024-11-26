package application.portfolio.utils;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

public class ParamsSplitter {

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
}
