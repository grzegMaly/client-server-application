package application.portfolio.clientmodule.Config;

import java.util.Properties;

public final class StyleProps {

    private static final String STYLE_PROPS = "/View/basePropsAndStyles.properties";
    private static Properties properties = null;

    static {
        properties = PropertiesLoader.getProperties(STYLE_PROPS);
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