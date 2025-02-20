package application.portfolio.clientmodule.Model.View.LeftBarCards;

import application.portfolio.clientmodule.Model.View.Page;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.concurrent.CompletableFuture;


public class Welcome extends VBox implements Page {

    private final Label welcomeLbl = new Label("Welcome To TeamLink App");

    @Override
    public CompletableFuture<Boolean> initializePage() {

        Platform.runLater(() -> this.getChildren().add(welcomeLbl));
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public void loadStyles() {
        this.setAlignment(Pos.CENTER);
        welcomeLbl.setStyle("-fx-font-size: 25; -fx-font-style: italic; -fx-text-fill: white");
    }

    @Override
    public Parent asParent() {
        return this;
    }
}