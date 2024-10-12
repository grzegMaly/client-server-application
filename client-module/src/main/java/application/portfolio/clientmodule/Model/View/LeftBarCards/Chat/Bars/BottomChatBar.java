package application.portfolio.clientmodule.Model.View.LeftBarCards.Chat.Bars;

import application.portfolio.clientmodule.Model.View.LeftBarCards.Chat.ChatBinder;
import application.portfolio.clientmodule.Model.View.Page;
import application.portfolio.clientmodule.utils.ExecutorServiceManager;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

public class BottomChatBar extends HBox implements Page {

    private final TextField messageTf = new TextField();
    private final Button sendBtn = new Button("Send");

    private static final ExecutorService executor =
            ExecutorServiceManager.createCachedThreadPool(BottomChatBar.class.getSimpleName());


    @Override
    public CompletableFuture<Boolean> initializePage() {

        try {
            Platform.runLater(() -> {
                messageTf.setPromptText("Write Message Here...");
                bindSizeProperties();

                this.setSpacing(10);
                this.setPadding(new Insets(15));
                this.getChildren().addAll(messageTf, sendBtn);
                this.setAlignment(Pos.CENTER_RIGHT);
            });

            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {

            //Todo: Improve
            e.printStackTrace();
            return CompletableFuture.completedFuture(false);
        }
    }

    public void setChatBinder(ChatBinder chatBinder) {
        chatBinder.withTextFieldSendButton(messageTf, sendBtn);
    }

    @Override
    public void bindSizeProperties() {

        messageTf.prefWidthProperty().bind(this.widthProperty().multiply(0.4));
    }

    @Override
    public void loadStyles() {
        this.getStyleClass().add("chatBarBG");
    }

    @Override
    public Parent asParent() {
        return this;
    }
}