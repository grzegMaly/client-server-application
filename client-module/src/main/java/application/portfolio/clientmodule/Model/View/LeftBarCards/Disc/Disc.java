package application.portfolio.clientmodule.Model.View.LeftBarCards.Disc;

import application.portfolio.clientmodule.Config.LoadStyles;
import application.portfolio.clientmodule.Model.View.Page;
import application.portfolio.clientmodule.Model.View.Scenes.start.MainScene;
import application.portfolio.clientmodule.TeamLinkApp;
import application.portfolio.clientmodule.utils.ExecutorServiceManager;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class Disc extends VBox implements Page {

    private final HBox topElements = new HBox();
    private Button backBtn;
    private Button uploadFileBtn;
    private Button uploadDirBtn;
    private Label currentPathLabel;
    private final Separator separator = new Separator();
    private final DiscStack discStack = new DiscStack();
    private final DiscBinder discBinder = new DiscBinder();
    private DiscController discController;

    private final ExecutorService executor =
            ExecutorServiceManager.createCachedThreadPool(this.getClass().getSimpleName());

    private Disc() {
    }

    @Override
    public CompletableFuture<Boolean> initializePage() {
        return CompletableFuture.runAsync(this::initializeComponents, executor)
                .thenApply(v -> true)
                .exceptionally(ext -> false);
    }

    private void initializeComponents() {

        CompletableFuture<Void> buttonFuture = CompletableFuture.runAsync(this::setUpButtons, executor);
        CompletableFuture<Void> labelFuture = CompletableFuture.runAsync(this::setUpLabel, executor);
        CompletableFuture<Void> stackFuture = CompletableFuture.runAsync(this::setUpDiscController, executor);

        CompletableFuture.allOf(buttonFuture, labelFuture, stackFuture)
                .thenRun(() -> {
                    discBinder.bindDiscController(discController);
                    discController.setDiscStack(discStack);
                    discController.navigateTo(Path.of("/"));
                    Platform.runLater(() -> {
                        Region spacer = new Region();
                        HBox.setHgrow(spacer, Priority.ALWAYS);
                        topElements.getChildren().addAll(backBtn, currentPathLabel, spacer, uploadFileBtn, uploadDirBtn);
                        this.getChildren().addAll(topElements, separator, discStack);
                    });
                });
    }

    private void setUpButtons() {
        backBtn = discBinder.bindBackBtn();
        uploadFileBtn = discBinder.bindUploadFileBtn();
        uploadDirBtn = discBinder.bindUploadDirBtn();
    }

    private void setUpLabel() {
        currentPathLabel = discBinder.bindPathLabel();
    }

    private void setUpDiscController() {
        discController = new DiscController();
    }

    @Override
    public Boolean loadStyleClass() {
        return LoadStyles.loadDiscSceneStyle(TeamLinkApp.getScene(MainScene.class));
    }

    @Override
    public void loadStyles() {
        this.getStyleClass().addAll("base", "baseBG");
        backBtn.getStyleClass().add("backBtn");
        uploadFileBtn.getStyleClass().add("uploadBtn");
        uploadDirBtn.getStyleClass().add("uploadBtn");
        currentPathLabel.getStyleClass().add("pathLbl");

        Insets insets = new Insets(10, 5, 0, 5);
        for (Node node: new Node[] {currentPathLabel, backBtn, uploadDirBtn, uploadFileBtn}) {
            HBox.setMargin(node, insets);
        }
    }

    @Override
    public Parent asParent() {
        return this;
    }
}