package application.portfolio.clientmodule.Connection;

import application.portfolio.clientmodule.Config.PropertiesLoader;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Infrastructure {

    private static final String PROTOCOL = "http://";
    private static final String WS_PROTOCOL = "ws://";
    private static final Map<String, Map<String, String>> gatewayData;
    private static final URI baseGatewayURI;

    static {
        gatewayData = loadGatewayData();
        baseGatewayURI = createBaseUri(getGatewayData(), PROTOCOL);
    }

    private static Map<String, Map<String, String>> loadGatewayData() {

        String path = "/Connection/connection.properties";
        Map<String, Map<String, String>> data =  PropertiesLoader.getProperties(path)
                .entrySet()
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

        return new ConcurrentHashMap<>(data);
    }

    public static String getHost(Map<String, String> data) {
        return data.get("host");
    }

    public static String getPort(Map<String, String> data) {
        return data.get("port");
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

    public static URI getWSBaseUri(Map<String, String> data) {
        return createBaseUri(data, WS_PROTOCOL);
    }

    public static URI getBaseUri(String spec) {
        return baseGatewayURI.resolve(spec);
    }

    private static URI createBaseUri(Map<String, String> data, String protocol) {
        String host = getHost(data);
        String port = getPort(data);

        String uri = "%s%s:%s".formatted(protocol, host, port);
        return URI.create(uri);
    }

    public static Map<String, String> getGatewayData() {
        return gatewayData.get("gateway");
    }

    public static Map<String, String> getGatewayChatWSData() {
        return gatewayData.get("gatewayChatWS");
    }

    public static String encodeParams(String params) {
        return URLEncoder.encode(params, StandardCharsets.UTF_8);
    }
}
