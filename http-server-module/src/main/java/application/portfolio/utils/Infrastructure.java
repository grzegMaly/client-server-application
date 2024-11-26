package application.portfolio.utils;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Infrastructure {

    private static final String PROTOCOL = "http://";
    private static final String OTHER_SERVERS_PROPS = "/config/otherServers.properties";
    private static Map<String, Map<String, String>> infrastructureData;

    static {

        infrastructureData = PropertiesLoader.getProperties(OTHER_SERVERS_PROPS).entrySet()
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

        infrastructureData = new ConcurrentHashMap<>(infrastructureData);
    }

    public static void addToMap(String key, Map<String, String> value) {

    }

    private static Map<String, String> getData(String key) {
        return new HashMap<>(infrastructureData.get(key));
    }

    public static String getHost(Map<String, String> data) {
        return data.get("host");
    }

    public static String getPort(Map<String, String> data) {
        return data.get("port");
    }

    public static String[] getTokens(Map<String, String> data) {

        String myToken = data.get("myToken");
        String receivedToken = data.get("receivedToken");

        if (myToken != null && receivedToken != null)
            return new String[]{myToken, receivedToken};

        return null;
    }

    public static URI getBaseUri(Map<String, String> data) {

        String host = getHost(data);
        String port = getPort(data);

        String uri = "%s%s:%s".formatted(PROTOCOL, host, port);
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

    public static Map<String, Map<String, String>> getInfrastructureData() {
        return new HashMap<>(infrastructureData);
    }

    public static Map<String, String> getCurrentServerData() {
        return getData("currentServer");
    }

    public static Map<String, String> getAuthorizationData() {
        return getData("authServer");
    }

    public static Map<String, String> getDatabaseData() {
        return getData("databaseServer");
    }
}