package application.portfolio.clientmodule.Model.View.LeftBarCards.Chat;

import application.portfolio.clientmodule.HttpClient.UserSession;
import application.portfolio.clientmodule.OtherElements.MessageDAO;
import application.portfolio.clientmodule.OtherElements.PersonDAO;
import application.portfolio.clientmodule.OtherElements.temp.Objects;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.control.ScrollBar;

import java.util.List;

public class ChatController {

    public static ChatView getChat(PersonDAO personDAO) {

        ChatView chatView = new ChatView(personDAO.getId().toString());

        List<MessageDAO> messages = loadMessages(personDAO);
        chatView.getItems().addAll(messages);

        chatView.skinProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> {
                for (ScrollBar scrollBar : chatView.lookupAll(".scroll-bar").toArray(new ScrollBar[0])) {
                    if (scrollBar.getOrientation().equals(Orientation.VERTICAL)) {
                        scrollBar.setVisible(false);
                    }
                }
            });
        });

        Platform.runLater(() -> {
            int visibleRowCount = calcVisibleRowCount(chatView);
            if (messages.size() > visibleRowCount) {
                chatView.scrollTo(messages.size() - 1);
                addSmoothScrolling(chatView);
            }
        });

        return chatView;
    }

    private static void addSmoothScrolling(ChatView chatView) {
        chatView.skinProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> {
                for (ScrollBar scrollBar : chatView.lookupAll(".scroll-bar").toArray(new ScrollBar[0])) {
                    if (scrollBar.getOrientation().equals(Orientation.VERTICAL)) {
                        scrollBar.valueProperty().addListener((observable, oldValue, newValue) -> {
                            if (Math.abs(newValue.doubleValue() - oldValue.doubleValue()) > 0.1) {
                                smoothScroll(scrollBar, oldValue.doubleValue(), newValue.doubleValue());
                            }
                        });
                    }
                }
            });
        });
    }

    private static void smoothScroll(ScrollBar scrollBar, double oldValue, double newValue) {
        scrollBar.setValue(newValue);
    }

    private static int calcVisibleRowCount(ChatView chatView) {

        double chatViewHeight = chatView.getHeight();
        double cellHeight = chatView.getFixedCellSize();

        if (cellHeight <= 0) {
            cellHeight = 100;
        }

        return (int) (chatViewHeight / cellHeight);
    }

    private static List<MessageDAO> loadMessages(PersonDAO personId) {

        //Todo: Connect to server
        PersonDAO userId = UserSession.getInstance().getLoggedInUser();
        return Objects.getMessages(userId, personId);
    }
}
