package application.portfolio.clientmodule.Config;

import javafx.scene.Scene;

import java.net.URL;

public final class LoadStyles {

    public static boolean loadLoginPageStyles(Scene scene) {
        return addStyles(scene, StyleProps.getLoginSceneStylesPath());
    }

    public static boolean loadMainSceneStyle(Scene scene) {
        return addStyles(scene, StyleProps.getMainSceneStylesPath());
    }

    public static Boolean loadLeftBarStyles(Scene scene) {
        return addStyles(scene, StyleProps.getLeftBarStylesPath());
    }

    public static Boolean loadChatSceneStyle(Scene scene) {
        return addStyles(scene, StyleProps.getChatPageStylesPath());
    }

    public static Boolean loadDiscSceneStyle(Scene scene) {
        return addStyles(scene, StyleProps.getDiscPageStylesPath());
    }

    public static Boolean loadNotesSceneStyle(Scene scene) {
        return addStyles(scene, StyleProps.getNotesPageStylesPath());
    }

    public static Boolean loadTasksSceneStyle(Scene scene) {
        return addStyles(scene, StyleProps.getTaskPageStylesPath());
    }

    public static Boolean loadManagementSceneStyle(Scene scene) {
        return addStyles(scene, StyleProps.getManagementPageStylesPath());
    }

    public static Boolean loadFriendsListClass(Scene scene) {
        return addStyles(scene, StyleProps.getFriendsListStylesPath());
    }

    private static boolean addStyles(Scene scene, String path) {

        try {
            URL styleResource = LoadStyles.class.getResource(path);
            if (styleResource == null) {
                return false;
            }

            String styleSheet = styleResource.toExternalForm();
            scene.getStylesheets().add(styleSheet);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}