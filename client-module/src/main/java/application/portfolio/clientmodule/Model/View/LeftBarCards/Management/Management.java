package application.portfolio.clientmodule.Model.View.LeftBarCards.Management;

import application.portfolio.clientmodule.Config.LoadStyles;
import application.portfolio.clientmodule.Model.View.Page;
import application.portfolio.clientmodule.Model.View.Scenes.start.MainScene;
import application.portfolio.clientmodule.TeamLinkApp;
import application.portfolio.clientmodule.utils.ExecutorServiceManager;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class Management extends HBox implements Page {

    private final Button manageUsersBtn = new Button("Manage Users");
    private final Button manageGroupsBtn = new Button("Manage Groups");
    private final Button manageUsersAndGroupsBtn = new Button("Manage Users & Groups");

    private final ButtonBar menuBar = new ButtonBar();
    private final VBox menuWrapper = new VBox();
    private final StackPane managingPages = new StackPane();

    private final ManagementBinder binder = new ManagementBinder();

    private final ExecutorService executor =
            ExecutorServiceManager.createCachedThreadPool(this.getClass().getSimpleName());

    private Management() {
    }

    @Override
    public CompletableFuture<Boolean> initializePage() {

        binder.setManagingPagesPane(managingPages);
        binder.setManageUsersBtn(manageUsersBtn);
        binder.setManageGroupsBtn(manageGroupsBtn);
        binder.setMangeUsersAndGroupsBtn(manageUsersAndGroupsBtn);

        menuBar.getButtons().addAll(manageUsersBtn, manageGroupsBtn, manageUsersAndGroupsBtn);
        menuWrapper.getChildren().addAll(menuBar, managingPages);

        Platform.runLater(() -> this.getChildren().add(menuWrapper));

        return CompletableFuture.supplyAsync(() -> {
            binder.initialize();
            return true;
        }, executor);
    }

    @Override
    public Boolean loadStyleClass() {
        return LoadStyles.loadManagementSceneStyle(TeamLinkApp.getScene(MainScene.class));
    }

    @Override
    public void loadStyles() {
        Platform.runLater(() -> {
            menuWrapper.setAlignment(Pos.TOP_LEFT);
            menuWrapper.setPadding(new Insets(10, 10, 10, 10));
            menuWrapper.setSpacing(10);
            menuBar.getButtons().forEach(e -> e.getStyleClass().add("managementBtn"));
        });
    }

    @Override
    public Parent asParent() {
        return this;
    }
}