package application.portfolio.clientmodule;

import application.portfolio.clientmodule.Config.StyleProps;
import application.portfolio.clientmodule.Connection.UserSession;
import application.portfolio.clientmodule.Model.View.Scenes.LoginPage.LoginScene;
import application.portfolio.clientmodule.Model.View.Page;
import application.portfolio.clientmodule.Model.View.Scenes.StartImage;
import application.portfolio.clientmodule.utils.ExecutorServiceManager;
import application.portfolio.clientmodule.utils.PageFactory;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class TeamLinkApp extends Application {

    private static Stage primaryStage;
    private final ExecutorService executor =
            ExecutorServiceManager.createCachedThreadPool(this.getClass().getSimpleName());
    private static final Map<Class<? extends Page>, Scene> sceneMap = new HashMap<>();
    private static boolean stageShown = false;

    public static void main(String[] args) {
        launch();
    }

    private Boolean checkConnection() {
        return UserSession.ping();
    }

    @Override
    public void start(Stage stage) throws Exception {

        primaryStage = stage;
        stage.setTitle(StyleProps.getAppName());
        runStartImage();

        CompletableFuture<Boolean> connectionFuture = CompletableFuture.supplyAsync(this::checkConnection, executor);
        CompletableFuture<LoginScene> loginSceneFuture =
                CompletableFuture.supplyAsync(() -> PageFactory.getInstance(LoginScene.class), executor);

        connectionFuture.thenCombine(loginSceneFuture, (isConnected, loginScene) -> {
            if (isConnected) {
                return loginScene;
            } else {
                throw new RuntimeException("error in start");
            }
        }).thenAccept(loginPage -> {
            if (loginPage != null) {
                useScene(loginPage.getClass());
            } else {
                System.out.println("Login Page is null");
            }
        }).exceptionally(ex -> null);
    }

    /**
     * Sets up start and min sizes
     */
    private void setUpStageSize() {

        Platform.runLater(() -> {
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();

            double screenWidth = bounds.getWidth();
            double screenHeight = bounds.getHeight();

            double screenStartWidth = screenWidth * 0.75;
            double screenStartHeight = screenHeight * 0.75;

            primaryStage.setWidth(screenStartWidth);
            primaryStage.setHeight(screenStartHeight);

            double stageMinWidth = screenWidth * 0.5;
            double stageMinHeight = screenHeight * 0.5;

            primaryStage.setMinWidth(stageMinWidth);
            primaryStage.setMinHeight(stageMinHeight);
        });
    }

    /**
     * Runs first scene while making connection
     */
    private void runStartImage() {

        StartImage startImage = PageFactory.getInstance(StartImage.class);
        setUpStageSize();
        if (startImage == null) {
            System.out.println("Error w runStartImage");
            throw new RuntimeException();
        }
        useScene(startImage.getClass());
    }

    public static Stage getMainStage() {
        return primaryStage;
    }

    public static void addScene(Class<? extends Page> page, Scene scene) {
        sceneMap.put(page, scene);
    }

    public static Scene getScene(Class<? extends Page> page) {
        return sceneMap.get(page);
    }

    /**
     * Dynamically changes the scene
     */
    public static void useScene(Class<? extends Page> className) {

        Platform.runLater(() -> {
            primaryStage.setScene(getScene(className));
            if (!stageShown) {
                primaryStage.show();
                stageShown = true;
            }
        });
    }

    @Override
    public void stop() throws Exception {
        ExecutorServiceManager.shutDownAll();
        super.stop();
    }
}