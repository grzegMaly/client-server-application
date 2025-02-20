package application.portfolio.clientmodule.Model.View.LeftBarCards.Chat.Bars;

import application.portfolio.clientmodule.Model.View.LeftBarCards.Chat.ChatBinder;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;



public class BottomChatBar extends HBox {

    private final TextField messageTf = new TextField();
    private final Button sendBtn = new Button("Send");

    public BottomChatBar(ChatBinder chatBinder) {

        chatBinder.withTextFieldSendButton(messageTf, sendBtn);
        messageTf.setPromptText("Write Message Here...");
        this.getChildren().addAll(messageTf, sendBtn);
    }

    public void loadStyles() {

        messageTf.prefWidthProperty().bind(this.widthProperty().multiply(0.4));

        this.setAlignment(Pos.CENTER_RIGHT);
        this.getStyleClass().add("bottomChatBarBG");

        HBox.setMargin(messageTf, new Insets(5, 15, 5, 10));
        HBox.setMargin(sendBtn, new Insets(5, 10, 5, 0));

        messageTf.getStyleClass().add("bottomChatBarTf");
        sendBtn.getStyleClass().add("bottomChatBarBtn");

        Platform.runLater(() -> {
            this.applyCss();
            this.layout();
        });
    }
}