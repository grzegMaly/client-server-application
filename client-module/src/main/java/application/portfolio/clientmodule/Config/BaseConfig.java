package application.portfolio.clientmodule.Config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class BaseConfig {

    private static final String PROPERTIES_FILE = "/basePropsAndStyles.properties";
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = BaseConfig.class.getResourceAsStream(PROPERTIES_FILE)) {

            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static String getAppName() {
        return getProperty("name");
    }

    public static String getAppScreenPath() {
        return getProperty("appScreen");
    }

    public static String getLoginSceneStylesPath() {
        return getProperty("loginScene");
    }

    public static String getMainSceneStylesPath() {
        return getProperty("mainScene");
    }

    public static String getChatPageStylesPath() {
        return getProperty("chatPage");
    }

    public static String getDiscPageStylesPath() {
        return getProperty("discPage");
    }

    public static String getNotesPageStylesPath() {
        return getProperty("notesPage");
    }

    public static String getTaskPageStylesPath() {
        return getProperty("tasksPage");
    }

    public static String getWatchPageStylesPath() {
        return getProperty("watchPage");
    }

    public static String getLeftBarStylesPath() {
        return getProperty("leftBar");
    }

    public static String getTopBarStylesPath() {
        return getProperty("topBar");
    }

    public static String getFriendsListStylesPath() {
        return getProperty("friendsList");
    }
}