package application.portfolio.clientmodule.Model.View.Scenes;

import application.portfolio.clientmodule.Config.BaseConfig;
import application.portfolio.clientmodule.Model.View.Page;
import application.portfolio.clientmodule.TeamLinkApp;
import application.portfolio.clientmodule.utils.ExecutorServiceManager;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

public class StartImage extends StackPane implements Page {

    private static final ImageView screen = new ImageView();
    private static Image image = null;
    private static final Stage mainStage = TeamLinkApp.getMainStage();
    private final ExecutorService executor =
            ExecutorServiceManager.createSingleThreadExecutor(this.getClass().getSimpleName());


    private StartImage() {}

    @Override
    public CompletableFuture<Boolean> loadPage() {

        return CompletableFuture.supplyAsync(this::getImage, executor)
        .thenApply(i -> {

            image = i;
            if (image == null) {
                //ToDo: Make it custom
                System.out.println("Error Loading ImageView");
                return false;
            }

            Platform.runLater(() -> {
                screen.setImage(image);
                screen.setSmooth(true);
                this.getChildren().add(screen);
                bindSizeProperties();
            });
            return true;
        }).exceptionally(ex -> {
            System.out.println("Error in StartImage, loginPage");
            return false;
        });
    }

    @Override
    public void bindSizeProperties() {

        Platform.runLater(() -> {
            screen.setFitHeight(mainStage.getHeight());
            screen.setFitWidth(mainStage.getWidth());
        });

        mainStage.widthProperty().addListener((obs, oV, nV) -> screen.setFitWidth(nV.doubleValue()));
        mainStage.heightProperty().addListener((obs, oV, nV) -> screen.setFitHeight(nV.doubleValue()));
    }

    private Image getImage() {

        String imageLoc = BaseConfig.getAppScreenPath();

        try {
            URL resource = StartImage.class.getResource(imageLoc);
            if (resource == null) {
                //ToDo: Make it custom
                throw new IllegalAccessException("resource not found " + imageLoc);
            }

            return new Image(resource.toExternalForm(), 0, 0, true, false);
        } catch (IllegalAccessException e) {
            //ToDo: Make it custom
            System.out.println("Cannot find start image");
        }
        return null;
    }

    @Override
    public Parent asParent() {
        return this;
    }

    @Override
    public AtomicBoolean usedAsScene() {
        return new AtomicBoolean(true);
    }
}