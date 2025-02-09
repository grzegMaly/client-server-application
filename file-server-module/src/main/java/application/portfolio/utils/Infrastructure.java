package application.portfolio.utils;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Infrastructure {

    private static final String PROTOCOL = "http://";
    private static final String OTHER_SERVERS_PROPS = "/config/otherServers/otherServers.properties";
    private static final Map<String, Map<String, String>> infrastructureData;

    static {

        infrastructureData = PropertiesLoader.getProperties(OTHER_SERVERS_PROPS).entrySet()
                .stream()
                .collect(Collectors.groupingBy(
                        entry -> entry.getKey().toString().split("\\.")[0],
                        Collectors.toMap(
                                entry -> entry.getKey().toString().split("\\.")[1],
                                entry -> entry.getValue().toString()
                        )
                ));
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

    public static URI getBaseUri(Map<String, String> data) {

        String host = getHost(data);
        String port = getPort(data);

        String uri = "%s%s:%s".formatted(PROTOCOL, host, port);
        return URI.create(uri);
    }

    public static Map<String, String> getCurrentServerData() {
        return getData("currentServer");
    }
    public static Map<String, String> getAuthorizationData() {
        return getData("authServer");
    }

    public static boolean isKnown(InetSocketAddress address) {

        String trustedIp = address.getAddress().getHostAddress();
        int trustedPort = address.getPort();

        for (Map<String, String> data : infrastructureData.values()) {

            String tempHost = getHost(data);
            int temPort = Integer.parseInt(getPort(data));
            var tempSocket = new InetSocketAddress(tempHost, temPort);

            tempHost = tempSocket.getAddress().getHostAddress();
            temPort = tempSocket.getPort();


            if (trustedIp.equals(tempHost) && trustedPort == temPort) {
                return true;
            }
        }
        return false;
    }
}