package application.portfolio.clientmodule.Model.View.LeftBarCards.Chat.Bars;

import application.portfolio.clientmodule.Model.View.LeftBarCards.Chat.ChatBinder;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

public class TopChatBar extends HBox {

    private static class Circles extends VBox {

        public Circles(double spacing) {
            super(spacing);
            this.setAlignment(Pos.CENTER);
            for (int i = 0; i < 3; i++) {
                this.getChildren().add(createCircle());
            }
        }

        private Circle createCircle() {
            Circle circle = new Circle(2);
            circle.setStyle("-fx-fill: #ffffff;");
            return circle;
        }
    }

    private final Label nameLbl = new Label();
    private final Button moreInfoBtn = new Button();


    public TopChatBar(ChatBinder chatBinder) {

        moreInfoBtn.setGraphic(new Circles(3));
        chatBinder.withReceiver(nameLbl, moreInfoBtn);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        moreInfoBtn.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(moreInfoBtn, Priority.ALWAYS);
        this.getChildren().addAll(nameLbl, spacer, moreInfoBtn);
    }

    public void loadStyles() {

        this.setAlignment(Pos.CENTER_LEFT);
        nameLbl.setPadding(new Insets(0, 0, 0, 10));
        this.getStyleClass().add("topBar");
        nameLbl.getStyleClass().add("topChatBarText");
        moreInfoBtn.getStyleClass().add("moreInfoChatBtn");

        Platform.runLater(() -> {
            this.applyCss();
            this.layout();
        });
    }
}