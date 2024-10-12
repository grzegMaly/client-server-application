package application.portfolio.clientmodule.Model.View.Bars;

import application.portfolio.clientmodule.Config.LoadStyles;
import application.portfolio.clientmodule.Model.View.Page;
import application.portfolio.clientmodule.Model.View.Scenes.start.MainScene;
import application.portfolio.clientmodule.TeamLinkApp;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class TopBar extends HBox implements Page {

    private final Button nameButton = new Button("Name");
    ContextMenu contextMenu = null;
    MenuItem settingsItem = new MenuItem("Settings");
    MenuItem logoutItem = new MenuItem("Logout");


    @Override
    public CompletableFuture<Boolean> initializePage() {

        try {

            Platform.runLater(() -> this.getChildren().add(nameButton));
            bindFields();

            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {

            //Todo: Improve
            e.printStackTrace();
            return CompletableFuture.completedFuture(false);
        }
    }

    public void bindFields() {

        nameButton.setOnAction(evt -> showContextMenu());
    }

    private void showContextMenu() {

        if (contextMenu == null) {
            contextMenu = new ContextMenu();

            settingsItem.setOnAction(evt -> {
                System.out.println("Clicked settings");
            });

            logoutItem.setOnAction(evt -> {
                System.out.println("Clicked logout");
            });

            contextMenu.getItems().addAll(settingsItem, logoutItem);
        }

        contextMenu.show(nameButton, nameButton.getScene().getWindow().getX() + nameButton.getLayoutX(),
                nameButton.getScene().getWindow().getY() + nameButton.getLayoutY() + nameButton.getHeight());
    }

    @Override
    public Boolean loadStyleClass() {
        return LoadStyles.loadTopBarStyles(TeamLinkApp.getScene(MainScene.class));
    }

    @Override
    public void loadStyles() {

        this.getStyleClass().add("topBarBG");
        nameButton.getStyleClass().add("topBarBtn");
    }

    @Override
    public Parent asParent() {
        return this;
    }

    @Override
    public AtomicBoolean usedAsScene() {
        return new AtomicBoolean(false);
    }
}