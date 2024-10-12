package application.portfolio.clientmodule.Model.View.LeftBarCards;

import application.portfolio.clientmodule.Model.View.Page;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;


public class Welcome extends VBox implements Page {


    Label welcomeLbl = new Label("Welcome To TeamLink App");

    @Override
    public CompletableFuture<Boolean> initializePage() {

        Platform.runLater(() -> this.getChildren().add(welcomeLbl));
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public Parent asParent() {
        return this;
    }

}