package application.portfolio.utils;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Infrastructure {

    private static final String PROTOCOL = "http://";
    private static final String OTHER_SERVERS_PROPS = "/config/otherServers.properties";
    private static final Map<String, Map<String, String>> httpData;

    static {

        httpData = initData(OTHER_SERVERS_PROPS);
    }

    private static Map<String, Map<String, String>> initData(String path) {
        Map<String, Map<String, String>> map = PropertiesLoader.getProperties(path).entrySet()
                .stream()
                .collect(Collectors.groupingBy(
                        entry -> entry.getKey().toString().split("\\.")[0],
                        Collectors.toMap(
                                entry -> entry.getKey().toString().split("\\.")[1],
                                entry -> entry.getValue().toString(),
                                (existing, replacement) -> existing,
                                ConcurrentHashMap::new
                        )
                ));

        return new ConcurrentHashMap<>(map);
    }

    private static Map<String, String> getData(String key) {
        return httpData.get(key);
    }

    public static String getHost(Map<String, String> data) {
        return data.get("host");
    }

    public static String getPort(Map<String, String> data) {
        return data.get("port");
    }

    public static URI getBaseUri(Map<String, String> data) {
        return createBaseUri(data, PROTOCOL);
    }

    private static URI createBaseUri(Map<String, String> data, String protocol) {
        String host = getHost(data);
        String port = getPort(data);

        String uri = "%s%s:%s".formatted(protocol, host, port);
        return URI.create(uri);
    }

    public static String uriSpecificPart(Map<String, String> data, String endpoint) {
        return uriSpecificPart(data, endpoint, "");
    }

    public static String uriSpecificPart(Map<String, String> data, String endpoint, String params) {

        String point = data.get(endpoint);
        if (point == null) {
            return "";
        }
        return point.concat(params);
    }

    public static Map<String, String> getCurrentWSServerData() {
        return getData("currentWebSocketServer");
    }

    public static Map<String, String> getDataBaseData() {
        return getData("databaseServer");
    }
}
