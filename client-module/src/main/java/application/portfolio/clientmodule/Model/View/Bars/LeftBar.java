package application.portfolio.clientmodule.Model.View.Bars;

import application.portfolio.clientmodule.Config.LoadStyles;
import application.portfolio.clientmodule.Connection.UserSession;
import application.portfolio.clientmodule.Model.Model.Person.Role;
import application.portfolio.clientmodule.Model.View.LeftBarCards.Chat.Chat;
import application.portfolio.clientmodule.Model.View.LeftBarCards.Disc.Disc;
import application.portfolio.clientmodule.Model.View.LeftBarCards.Notes.Notes;
import application.portfolio.clientmodule.Model.View.LeftBarCards.Tasks.Tasks;
import application.portfolio.clientmodule.Model.View.LeftBarCards.Management.Management;
import application.portfolio.clientmodule.Model.View.Page;
import application.portfolio.clientmodule.Model.View.Scenes.start.MainScene;
import application.portfolio.clientmodule.TeamLinkApp;
import application.portfolio.clientmodule.utils.PageFactory;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.concurrent.CompletableFuture;

public class LeftBar extends VBox implements Page {

    private final Button chatBtn = new Button("Chat");
    private final Button notesBtn = new Button("Notes");
    private final Button discBtn = new Button("Disc");
    private final Button tasksBtn = new Button("Tasks");
    private Button managementBtn;

    private LeftBar() {
    }


    @Override
    public CompletableFuture<Boolean> initializePage() {

        return CompletableFuture.runAsync(() ->
                        Platform.runLater(() ->
                                this.getChildren().addAll(chatBtn, notesBtn, discBtn, tasksBtn)))
                .thenRunAsync(this::setActions)
                .thenApply(v -> true)
                .exceptionally(ext -> false);
    }

    public void setActions() {

        chatBtn.setOnAction(evt -> {
            Chat chatScene = PageFactory.getInstance(Chat.class);
            useElement(chatScene);
        });

        notesBtn.setOnAction(evt -> {
            Notes notesScene = PageFactory.getInstance(Notes.class);
            useElement(notesScene);
        });

        discBtn.setOnAction(evt -> {
            Disc discScene = PageFactory.getInstance(Disc.class);
            useElement(discScene);
        });

        tasksBtn.setOnAction(evt -> {
            Tasks tasksScene = PageFactory.getInstance(Tasks.class);
            useElement(tasksScene);
        });

        Role role = UserSession.getInstance().getLoggedInUser().getRole();
        if (role == Role.ADMIN) {
            managementBtn = new Button("Management");
            Platform.runLater(() -> this.getChildren().add(managementBtn));
            managementBtn.setOnAction(evt -> {
                Management managementScene = PageFactory.getInstance(Management.class);
                useElement(managementScene);
            });
        }
    }

    private void useElement(Node element) {
        if (element != null) {
            MainScene.useElement(element);
        }
    }

    @Override
    public Boolean loadStyleClass() {
        return LoadStyles.loadLeftBarStyles(TeamLinkApp.getScene(MainScene.class));
    }

    @Override
    public void loadStyles() {

        Platform.runLater(() -> {
            this.getStyleClass().add("leftBarBG");

            for (var node : this.getChildren()) {
                if (node instanceof Button btn) {
                    btn.getStyleClass().add("leftBarBtn");
                }
            }

            if (managementBtn != null) {
                managementBtn.getStyleClass().add("leftBarBtn");
            }
        });
    }

    @Override
    public void bindSizeProperties() {

        Platform.runLater(() -> {
            for (var node : this.getChildren()) {
                if (node instanceof Button btn) {

                    btn.prefHeightProperty().bind(this.heightProperty().multiply(0.05));
                    btn.prefWidthProperty().bind(this.widthProperty().multiply(0.8));
                }
            }
        });
    }

    @Override
    public Parent asParent() {
        return this;
    }
}