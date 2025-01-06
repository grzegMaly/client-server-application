package application.portfolio.utils;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class Infrastructure {

    private static final String PROTOCOL = "http://";
    private static final String WB_PROTOCOL = "ws://";
    private static final String OTHER_SERVERS_PROPS = "/config/otherServers.properties";
    private static final String WS_SERVERS_PROPS = "/config/wsServers.properties";
    private static final Map<String, Map<String, String>> httpData;
    private static final Map<String, Map<String, String>> wsData;

    static {
        httpData = initData(OTHER_SERVERS_PROPS);
        wsData = initData(WS_SERVERS_PROPS);
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

    public static Map<String, String> getData(String key, int mapNum) {
        return mapNum == 0 ? httpData.get(key) : wsData.get(key);
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

    public static URI getWBBaseUri(Map<String, String> data) {
        return createBaseUri(data, WB_PROTOCOL);
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

    public static Map<String, String> getCurrentServerData() {
        return getData("currentServer", 0);
    }

    public static Map<String, String> getAuthorizationData() {
        return getData("authServer", 0);
    }

    public static Map<String, String> getDatabaseData() {
        return getData("databaseServer", 0);
    }
    public static Map<String, String> getFileServerData() {
        return getData("fileServer", 0);
    }

    public static Map<String, String> getCurrentWSServerData() {
        return getData("currentWebSocketServer", 1);
    }

    public static Map<String, String> getChatWSData() {
        return getData("ChatWebSocketServer", 1);
    }
}