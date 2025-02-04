package application.portfolio.clientmodule.Model.View.LeftBarCards.Chat;

import application.portfolio.clientmodule.Model.Model.Chat.MessageDAO;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class MessageBox extends HBox {

    private final Label messageContent = new Label();

    public MessageBox(MessageDAO messageDAO) {

        this.getChildren().add(messageContent);
        messageContent.setText(messageDAO.getContent());
    }

    public void setMessageBoxStyle(boolean received) {

        messageContent.setWrapText(true);

        if (received) {
            this.setAlignment(Pos.CENTER_LEFT);
            messageContent.getStyleClass().add("receivedMessage");
        } else {
            this.setAlignment(Pos.CENTER_RIGHT);
            messageContent.getStyleClass().add("sentMessage");
        }
    }

    public DoubleProperty labelMaxWidthProperty() {
        return messageContent.maxWidthProperty();
    }
}
