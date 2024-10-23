package application.portfolio.clientmodule.Connection;

import application.portfolio.clientmodule.Config.PropertiesLoader;

import java.util.Properties;

public class ConnectionProps {

    private static final String CONNECTION_PROPS = "/Connection/connection.properties";
    private static final String ENDPOINT_PROPS = "/Connection/endpoints.properties";

    private static final Properties properties;

    static {
        properties = PropertiesLoader.getProperties(CONNECTION_PROPS);
    }

    private static String getProperty(String key) {
        if (!properties.contains(key)) {
            properties.putAll(PropertiesLoader.getProperties(ENDPOINT_PROPS));
        }

        return properties.getProperty(key);
    }

    public static String getPingEndpoint() {
        return getProperty("ping");
    }

    public static String getLoginEndpoint() {
        return getProperty("login");
    }
}
