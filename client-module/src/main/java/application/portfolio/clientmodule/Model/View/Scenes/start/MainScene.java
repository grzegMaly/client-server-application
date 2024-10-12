package application.portfolio.clientmodule.Model.View.Scenes.start;

import application.portfolio.clientmodule.Config.LoadStyles;
import application.portfolio.clientmodule.Model.View.Bars.LeftBar;
import application.portfolio.clientmodule.Model.View.Bars.TopBar;
import application.portfolio.clientmodule.Model.View.LeftBarCards.Welcome;
import application.portfolio.clientmodule.Model.View.Page;
import application.portfolio.clientmodule.TeamLinkApp;
import application.portfolio.clientmodule.utils.ExecutorServiceManager;
import application.portfolio.clientmodule.utils.PageFactory;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainScene extends VBox implements Page {

    private LeftBar leftBar = null;
    private TopBar topBar = null;
    private Welcome welcome = null;
    private static final StackPane templatesHolder = new StackPane();
    private final Stage mainStage = TeamLinkApp.getMainStage();

    private static final ExecutorService executor =
            ExecutorServiceManager.createCachedThreadPool(MainScene.class.getSimpleName());

    private MainScene() {
    }

    @Override
    public CompletableFuture<Boolean> initializePage() {

        CompletableFuture<TopBar> topBarFuture =
                CompletableFuture.supplyAsync(() -> PageFactory.getInstance(TopBar.class), executor);
        CompletableFuture<LeftBar> leftBarFuture =
                CompletableFuture.supplyAsync(() -> PageFactory.getInstance(LeftBar.class), executor);
        CompletableFuture<Welcome> welcomeFuture =
                CompletableFuture.supplyAsync(() -> PageFactory.getInstance(Welcome.class), executor);

        return CompletableFuture.allOf(topBarFuture, leftBarFuture, welcomeFuture)
                .thenCompose(v -> {
                    leftBar = leftBarFuture.join();
                    topBar = topBarFuture.join();
                    welcome = welcomeFuture.join();

                    return CompletableFuture.completedFuture(
                            leftBar != null && topBar != null && welcome != null
                    );
                })
                .thenApply(success -> {

                    if (success) {
                        Platform.runLater(() -> {

                            HBox hBox = new HBox();
                            useElement(welcome);
                            hBox.getChildren().addAll(leftBar, templatesHolder);
                            this.getChildren().addAll(topBar, hBox);
                            bindSizeProperties();
                        });
                    }
                    return success;
                }).exceptionally(ex -> {
                    //Todo: make it custom
                    System.out.println("initializePage, " + this.getClass().getSimpleName());
                    ex.printStackTrace();
                    return false;
                });
    }

    @Override
    public void bindSizeProperties() {
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();

        double screenWidth = screenBounds.getWidth();
        double screenHeight = screenBounds.getHeight();

        topBar.setMinHeight(screenHeight * 0.03);
        topBar.setMaxHeight(screenHeight * 0.03);

        leftBar.setPrefWidth(screenWidth * 0.1);
        leftBar.setMinWidth(screenWidth * 0.1);
        leftBar.setMaxWidth(screenWidth * 0.1);

        leftBar.setPrefHeight(screenHeight - topBar.getHeight());
        VBox.setVgrow(leftBar, Priority.ALWAYS);

        templatesHolder.setPrefWidth(screenWidth - leftBar.getPrefWidth());
        templatesHolder.setPrefHeight(screenHeight - topBar.getHeight());
        HBox.setHgrow(templatesHolder, Priority.ALWAYS);
        VBox.setVgrow(templatesHolder, Priority.ALWAYS);

        mainStage.widthProperty().addListener((obs, oldVal, newVal) -> adjustSize());
        mainStage.heightProperty().addListener((obs, oldVal, newVal) -> adjustSize());
    }

    private void adjustSize() {
        double availableWidth = mainStage.getWidth() - leftBar.getPrefWidth();
        double availableHeight = mainStage.getHeight() - topBar.getHeight();

        templatesHolder.setPrefWidth(availableWidth);
        templatesHolder.setPrefHeight(availableHeight);

        leftBar.setPrefHeight(availableHeight);
    }

    @Override
    public Boolean loadStyleClass() {
        return LoadStyles.loadMainSceneStyle(TeamLinkApp.getScene(this.getClass()));
    }

    @Override
    public void loadStyles() {
        welcome.getStyleClass().add("template");
    }

    @Override
    public Parent asParent() {
        return this;
    }

    @Override
    public AtomicBoolean usedAsScene() {
        return new AtomicBoolean(true);
    }

    public static void useElement(Node element) {

        if (!templatesHolder.getChildren().contains(element)) {
            templatesHolder.getChildren().add(element);
        }

        templatesHolder.getChildren().forEach(e -> e.setVisible(e.equals(element)));
    }
}