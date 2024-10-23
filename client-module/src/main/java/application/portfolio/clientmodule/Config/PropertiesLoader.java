package application.portfolio.clientmodule.Config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class PropertiesLoader {

    public static Properties getProperties(String path) {

        Properties properties;
        try (InputStream input = PropertiesLoader.class.getResourceAsStream(path)) {
            properties = new Properties();
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties;
    }
}
