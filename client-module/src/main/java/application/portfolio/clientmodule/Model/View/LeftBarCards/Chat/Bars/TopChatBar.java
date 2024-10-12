package application.portfolio.clientmodule.Model.View.LeftBarCards.Chat.Bars;

import application.portfolio.clientmodule.Model.View.LeftBarCards.Chat.ChatBinder;
import application.portfolio.clientmodule.OtherElements.PersonDAO;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class TopChatBar extends HBox {

    private static class Circles extends VBox {

        public Circles(double spacing) {
            super(spacing);
            this.getChildren().addAll(createCircle(), createCircle(), createCircle());
        }

        private Circle createCircle() {
            Circle circle = new Circle(2);
            circle.setFill(Color.BLACK);
            return circle;
        }
    }

    private final Label nameLbl = new Label();
    private final Button moreInfoBtn = new Button();
    private ChatBinder chatBinder = null;


    public TopChatBar(ChatBinder chatBinder) {

        this.chatBinder = chatBinder;

        chatBinder.withReceiver(nameLbl, moreInfoBtn);
        moreInfoBtn.setGraphic(new Circles(3));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        this.getChildren().addAll(nameLbl, spacer, moreInfoBtn);

        loadStyles();
    }

    private void loadStyles() {

        this.getStyleClass().add("friendsListBG");
        nameLbl.getStyleClass().add("topChatBarText");
    }
}