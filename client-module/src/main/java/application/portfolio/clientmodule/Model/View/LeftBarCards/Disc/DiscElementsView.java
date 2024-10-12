package application.portfolio.clientmodule.Model.View.LeftBarCards.Disc;

import application.portfolio.clientmodule.Model.View.Page;
import application.portfolio.clientmodule.utils.ExecutorServiceManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

public class DiscElementsView extends VBox implements Page {

    private final Button backBtn = new Button("Back");
    private final TilePane discElements = new TilePane();
    private final ExecutorService executor =
            ExecutorServiceManager.createCachedThreadPool(this.getClass().getSimpleName());

    @Override
    public CompletableFuture<Boolean> initializePage() {

        //Todo: completable futures for downloading elements from server
        CompletableFuture<Boolean> loadElementsFuture = CompletableFuture.supplyAsync(this::loadDisc, executor);

        discElements.setPrefColumns(5);
        discElements.setPrefRows(3);
        discElements.setTileAlignment(Pos.CENTER);

        ObservableList<Node> list = FXCollections.observableArrayList();

        for (int i = 0; i < 15; i++) {
            list.add(new Rectangle(50, 50, new Color(new Random().nextDouble(),
                    new Random().nextDouble(), new Random().nextDouble(), 1)));
        }

        Platform.runLater(() -> {

            discElements.getChildren().addAll(list);
            this.getChildren().addAll(backBtn, discElements);
        });

        //Todo: just to do, to implement
        return CompletableFuture.completedFuture(true);
    }

    public Boolean loadDisc() {

        return true;
    }

    @Override
    public Parent asParent() {
        return this;
    }

}