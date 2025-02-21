package application.portfolio.clientmodule.Model.View.Scenes.start;

import application.portfolio.clientmodule.Config.LoadStyles;
import application.portfolio.clientmodule.Model.View.Bars.LeftBar;
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
    private Welcome welcome = null;
    private static final StackPane templatesHolder = new StackPane();
    private final Stage mainStage = TeamLinkApp.getMainStage();

    private static final ExecutorService executor =
            ExecutorServiceManager.createCachedThreadPool(MainScene.class.getSimpleName());

    private MainScene() {
    }

    @Override
    public CompletableFuture<Boolean> initializePage() {

        CompletableFuture<LeftBar> leftBarFuture =
                CompletableFuture.supplyAsync(() -> PageFactory.getInstance(LeftBar.class), executor);
        CompletableFuture<Welcome> welcomeFuture =
                CompletableFuture.supplyAsync(() -> PageFactory.getInstance(Welcome.class), executor);

        return leftBarFuture
                .thenCombine(welcomeFuture, (leftBarResult, welcomeResult) -> {
                    this.leftBar = leftBarResult;
                    this.welcome = welcomeResult;
                    return (leftBarResult != null && welcomeResult != null);
                })
                .thenApply(success -> {
                    if (success) {
                        Platform.runLater(() -> {

                            HBox hBox = new HBox();
                            useElement(welcome);
                            hBox.getChildren().addAll(leftBar, templatesHolder);
                            this.getChildren().add(hBox);
                            bindSizeProperties();
                        });
                    }
                    return success;
                })
                .thenRun(() -> Platform.runLater(this::bindSizeProperties))
                .thenRun(() -> Platform.runLater(leftBar::bindSizeProperties))
                .thenApply(v -> true)
                .exceptionally(ex -> false);
    }

    @Override
    public void bindSizeProperties() {
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        double screenWidth = screenBounds.getWidth();

        leftBar.setPrefWidth(screenWidth * 0.1);
        leftBar.setMinWidth(screenWidth * 0.1);
        leftBar.setMaxWidth(screenWidth * 0.1);

        leftBar.prefHeightProperty().bind(mainStage.heightProperty());
        VBox.setVgrow(leftBar, Priority.ALWAYS);

        templatesHolder.setPrefWidth(screenWidth - leftBar.getPrefWidth());
        templatesHolder.prefHeightProperty().bind(mainStage.heightProperty());

        HBox.setHgrow(templatesHolder, Priority.ALWAYS);
        VBox.setVgrow(templatesHolder, Priority.ALWAYS);

        mainStage.widthProperty().addListener((obs, oldVal, newVal) -> adjustSize());
        mainStage.heightProperty().addListener((obs, oldVal, newVal) -> adjustSize());

        adjustSize();
    }


    private void adjustSize() {
        double availableWidth = mainStage.getWidth() - leftBar.getPrefWidth();
        templatesHolder.setPrefWidth(availableWidth);
    }


    @Override
    public Boolean loadStyleClass() {
        return LoadStyles.loadMainSceneStyle(TeamLinkApp.getScene(this.getClass()));
    }

    @Override
    public void loadStyles() {
        templatesHolder.getStyleClass().add("mainSceneBg");
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