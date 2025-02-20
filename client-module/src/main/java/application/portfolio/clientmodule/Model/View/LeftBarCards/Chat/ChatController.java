package application.portfolio.clientmodule.Model.View.LeftBarCards.Chat;

import application.portfolio.clientmodule.Connection.UserSession;
import application.portfolio.clientmodule.Model.Request.Chat.Chat.ChatRequestViewModel;
import application.portfolio.clientmodule.Model.Model.Chat.MessageDAO;
import application.portfolio.clientmodule.Model.Model.Person.Person;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.StackPane;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class ChatController {

    private static Person actualUser;
    private static ChatView actualChat;

    public static ChatView createChat(Person personDAO, ChatRequestViewModel viewModel) {

        ChatView chatView = new ChatView(personDAO.getUserId(), viewModel);

        loadChatHistory(chatView, personDAO);
        addScrollListener(chatView, personDAO);

        chatView.skinProperty().addListener((obs, oldVal, newVal) ->
                Platform.runLater(() -> {
                    for (ScrollBar scrollBar : chatView.lookupAll(".scroll-bar").toArray(new ScrollBar[0])) {
                        if (scrollBar.getOrientation().equals(Orientation.VERTICAL)) {
                            scrollBar.setVisible(false);
                        }
                    }
                }));

        List<MessageDAO> messages = chatView.getItems();
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
        chatView.skinProperty().addListener((obs, oldVal, newVal) ->
                Platform.runLater(() -> {
                    for (ScrollBar scrollBar : chatView.lookupAll(".scroll-bar").toArray(new ScrollBar[0])) {
                        if (scrollBar.getOrientation().equals(Orientation.VERTICAL)) {
                            scrollBar.valueProperty().addListener((observable, oldValue, newValue) -> {
                                if (Math.abs(newValue.doubleValue() - oldValue.doubleValue()) > 0.1) {
                                    smoothScroll(scrollBar, newValue.doubleValue());
                                }
                            });
                        }
                    }
                }));
    }

    private static void smoothScroll(ScrollBar scrollBar, double newValue) {
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

    private static void loadChatHistory(ChatView chatView, Person receiver) {

        if (!chatView.hasMoreMessages()) {
            return;
        }

        ChatRequestViewModel viewModel = new ChatRequestViewModel();
        viewModel.setSender(UserSession.getInstance().getLoggedInUser());
        viewModel.setReceiver(receiver);

        List<MessageDAO> messages = viewModel.loadChatHistory(chatView.getOffset(), ChatView.LIMIT);

        if (messages == null || messages.isEmpty()) {
            chatView.setHasMoreMessages(false);
            return;
        }

        chatView.setHasMoreMessages(messages.size() >= ChatView.LIMIT);

        Platform.runLater(() -> {
            chatView.getItems().addAll(0, messages);
            chatView.incrementOffset();
        });
    }

    private static void addScrollListener(ChatView chatView, Person personDAO) {
        chatView.skinProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                for (ScrollBar scrollBar : chatView.lookupAll(".scroll-bar").toArray(new ScrollBar[0])) {
                    if (scrollBar.getOrientation() == Orientation.VERTICAL) {
                        scrollBar.valueProperty().addListener((observable, oldValue, newValue1) -> {
                            if (newValue1.doubleValue() <= scrollBar.getMin()) {
                                loadChatHistory(chatView, personDAO);
                            }
                        });
                    }
                }
            }
        });
    }

    public static void handleBadMessage(MessageDAO messageDAO) {

        UUID id = messageDAO.getTempId();
        Platform.runLater(() -> findChats()
                .filter(chatView -> chatView.getItems().stream()
                        .anyMatch(message -> message.getTempId().equals(id)))
                .findFirst()
                .ifPresent(chatView -> chatView.getItems().removeIf(m -> id.equals(m.getTempId())))
        );
    }

    public static void addMessageToChat(MessageDAO messageDAO) {

        Person sender = messageDAO.getSender();
        UUID id = sender.getUserId();

        if (id.equals(UserSession.getInstance().getLoggedInUser().getUserId())) {
            Platform.runLater(() -> actualChat.getItems().add(messageDAO));
        } else {
            if (actualChat != null && id.equals(actualChat.getChatId())) {
                Platform.runLater(() -> actualChat.getItems().add(messageDAO));
            } else {
                Platform.runLater(() -> findChats()
                        .filter(chatView -> chatView.getChatId().equals(id))
                        .findFirst()
                        .ifPresentOrElse(chatView -> chatView.getItems().add(messageDAO),
                                () -> {
                                    ChatView chatView = Friends.createChat(sender);
                                    chatView.getItems().add(messageDAO);
                                }
                        )
                );
            }
        }
    }

    private static Stream<ChatView> findChats() {

        StackPane chats = Chat.getChats();
        return chats.getChildren()
                .stream()
                .filter(c -> c instanceof ChatView)
                .map(c -> (ChatView) c);
    }

    public static Person getActualUser() {
        return actualUser;
    }

    public static void setActualUser(Person actualUser) {
        ChatController.actualUser = actualUser;
    }

    public static void setActualChat(ChatView chatView) {
        ChatController.actualChat = chatView;
    }
}
