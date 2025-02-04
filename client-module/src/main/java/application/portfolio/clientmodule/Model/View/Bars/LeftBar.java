package application.portfolio.clientmodule.Model.View.Bars;

import application.portfolio.clientmodule.Config.LoadStyles;
import application.portfolio.clientmodule.Model.View.LeftBarCards.Chat.Chat;
import application.portfolio.clientmodule.Model.View.LeftBarCards.Disc.Disc;
import application.portfolio.clientmodule.Model.View.LeftBarCards.Notes.Notes;
import application.portfolio.clientmodule.Model.View.LeftBarCards.Tasks.Tasks;
import application.portfolio.clientmodule.Model.View.LeftBarCards.Watch.Watch;
import application.portfolio.clientmodule.Model.View.Page;
import application.portfolio.clientmodule.Model.View.Scenes.start.MainScene;
import application.portfolio.clientmodule.TeamLinkApp;
import application.portfolio.clientmodule.utils.PageFactory;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class  LeftBar extends VBox implements Page {

    private final Button chatBtn = new Button("Chat");
    private final Button notesBtn = new Button("Notes");
    private final Button discBtn = new Button("Disc");
    private final Button watchBtn = new Button("Watch");
    private final Button tasksBtn = new Button("Tasks");

    private LeftBar() {
    }


    @Override
    public CompletableFuture<Boolean> initializePage() {

        try {

            Platform.runLater(() -> this.getChildren().addAll(chatBtn, notesBtn, discBtn, watchBtn, tasksBtn));
            bindFields();

            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {
            //Todo: Make it custom
            System.out.println("Error in " + this.getClass().getSimpleName() + ", initializePage");
            e.printStackTrace();
            return CompletableFuture.completedFuture(false);
        }
    }

    public void bindFields() {

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

        watchBtn.setOnAction(evt -> {
            Watch watchScene = PageFactory.getInstance(Watch.class);
            useElement(watchScene);
        });

        tasksBtn.setOnAction(evt -> {
            Tasks tasksScene = PageFactory.getInstance(Tasks.class);
            useElement(tasksScene);
        });
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
                if (node instanceof Button btn)
                    btn.getStyleClass().add("leftBarBtn");
            }

            //Todo: Finish
            /*chatBtn.getStyleClass().add("leftBarBtn");
            notesBtn.getStyleClass().add("leftBarBtn");
            discBtn.getStyleClass().add("leftBarBtn");
            watchBtn.getStyleClass().add("leftBarBtn");
            tasksBtn.getStyleClass().add("leftBarBtn"); */
        });
    }

    @Override
    public Parent asParent() {
        return this;
    }
}