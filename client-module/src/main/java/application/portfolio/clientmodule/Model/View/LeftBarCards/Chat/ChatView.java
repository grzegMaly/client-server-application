package application.portfolio.clientmodule.Model.View.LeftBarCards.Chat;

import application.portfolio.clientmodule.Connection.UserSession;
import application.portfolio.clientmodule.Model.Request.Chat.Chat.ChatRequestViewModel;
import application.portfolio.clientmodule.Model.Model.Chat.MessageDAO;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;

import java.util.UUID;

public class ChatView extends ListView<MessageDAO> {

    private final UUID chatId;
    private int offset = 0;
    public static final int LIMIT = 15;
    private boolean hasMoreMessages = true;
    private final ChatRequestViewModel viewModel;

    public ChatView(UUID id, ChatRequestViewModel viewModel) {
        this.chatId = id;
        this.viewModel = viewModel;
        initListCell();
        setActionStyles();
    }

    public UUID getChatId() {
        return chatId;
    }

    public int getOffset() {
        return offset;
    }

    public void incrementOffset() {
        this.offset++;
    }

    public void resetOffset() {
        this.offset = 0;
        this.hasMoreMessages = true;
    }

    public boolean hasMoreMessages() {
        return hasMoreMessages;
    }

    public void setHasMoreMessages(boolean hasMoreMessages) {
        this.hasMoreMessages = hasMoreMessages;
    }

    public ChatRequestViewModel getViewModel() {
        return viewModel;
    }

    private void setActionStyles() {

        this.getStyleClass().add("listCellBG");

        this.setMouseTransparent(false);
        this.setFocusTraversable(true);

        this.skinProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> {
                for (ScrollBar scrollBar : this.lookupAll(".scroll-bar").toArray(new ScrollBar[0])) {
                    if (scrollBar.getOrientation().equals(Orientation.HORIZONTAL) ||
                            scrollBar.getOrientation().equals(Orientation.VERTICAL)) {
                        scrollBar.lookupAll(".increment-button").forEach(button -> {
                            button.setVisible(false);
                            button.setManaged(false);
                        });
                        scrollBar.lookupAll(".decrement-button").forEach(button -> {
                            button.setVisible(false);
                            button.setManaged(false);
                        });
                    }
                }
            });
        });
    }

    private void initListCell() {
        this.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(MessageDAO message, boolean empty) {
                super.updateItem(message, empty);
                if (message == null || empty) {
                    setText(null);
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                } else {

                    MessageBox messageBox = new MessageBox(message);
                    messageBox.labelMaxWidthProperty().bind(this.widthProperty().multiply(0.6));

                    messageBox.setMessageBoxStyle(!message.getSender().equals(UserSession.getInstance().getLoggedInUser()));
                    setGraphic(messageBox);
                    getStyleClass().add("listCellBG");
                }
            }
        });
    }
}
