package application.portfolio.clientmodule.Connection;

import application.portfolio.clientmodule.Config.PropertiesLoader;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Infrastructure {

    private static final String PROTOCOL = "http://";
    private static final Map.Entry<String, Map<String, String>> gatewayData;
    private static final Map<String, String> endpointsData;

    static {
        gatewayData = loadGatewayData();
        endpointsData = loadEndpointData();
    }

    private static Map.Entry<String, Map<String, String>> loadGatewayData() {

        String path = "/Connection/connection.properties";
        return PropertiesLoader.getProperties(path)
                .entrySet()
                .stream()
                .collect(Collectors.groupingBy(
                        entry -> entry.getKey().toString().split("\\.")[0],
                        Collectors.toMap(
                                entry -> entry.getKey().toString().split("\\.")[1],
                                entry -> entry.getValue().toString()
                        )
                ))
                .entrySet()
                .stream().findFirst().orElse(null);
    }

    private static Map<String, String> loadEndpointData() {

        String path = "/Connection/endpoints.properties";
        return PropertiesLoader.getProperties(path).entrySet()
                .stream()
                .collect(Collectors.toMap(
                        entry -> (String) entry.getKey(),
                        entry -> (String) entry.getValue()
                ));
    }

    public static String getHost(Map<String, String> data) {
        return data.get("host");
    }

    public static String getPort(Map<String, String> data) {
        return data.get("port");
    }

    public static URI getBaseUri(Map<String, String> data) {

        String host = getHost(data);
        String port = getPort(data);

        String uri = "%s%s:%s".formatted(PROTOCOL, host, port);
        return URI.create(uri);
    }

    public static Map<String, String> getGatewayData() {
        return new HashMap<>(gatewayData.getValue());
    }

    public static String getPingEndpoint() {
        return endpointsData.get("ping");
    }

    public static String getLoginEndpoint() {
        return endpointsData.get("login");
    }
}
