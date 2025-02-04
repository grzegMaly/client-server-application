package application.portfolio.clientmodule.Model.Request.Chat.Chat;

import application.portfolio.clientmodule.Connection.Infrastructure;
import application.portfolio.clientmodule.Connection.UserSession;
import application.portfolio.clientmodule.Connection.WebSocket.Listeners.ChatWebSocketListener;
import application.portfolio.clientmodule.Connection.WebSocket.WebSocketClientHolder;
import application.portfolio.clientmodule.Model.Request.Chat.Chat.ChatRequest.ChatRequest;
import application.portfolio.clientmodule.Model.View.LeftBarCards.Chat.ChatController;
import application.portfolio.clientmodule.Model.Model.Chat.MessageDAO;
import application.portfolio.clientmodule.Model.Model.Person.PersonDAO;
import application.portfolio.clientmodule.utils.DataParser;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ChatRequestViewModel {

    private final SimpleObjectProperty<PersonDAO> sender = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<PersonDAO> receiver = new SimpleObjectProperty<>();

    private final SimpleStringProperty message = new SimpleStringProperty();
    private final SimpleListProperty<MessageDAO> messages = new SimpleListProperty<>();

    private final ChatRequestModel model = new ChatRequestModel();
    private final ChatRequestConverter converter = new ChatRequestConverter();

    public PersonDAO getSender() {
        return sender.get();
    }

    public SimpleObjectProperty<PersonDAO> senderProperty() {
        return sender;
    }

    public void setSender(PersonDAO sender) {
        this.sender.set(sender);
    }

    //-------------------------------------------------------------------------

    public PersonDAO getReceiver() {
        return receiver.get();
    }

    public SimpleObjectProperty<PersonDAO> receiverProperty() {
        return receiver;
    }

    public void setReceiver(PersonDAO receiver) {
        this.receiver.set(receiver);
    }

    //-------------------------------------------------------------------------

    public String getMessage() {
        return message.get();
    }

    public SimpleStringProperty messageProperty() {
        return message;
    }

    public void setMessage(String message) {
        this.message.set(message);
    }

    //-------------------------------------------------------------------------

    public ObservableList<MessageDAO> getMessages() {
        return messages.get();
    }

    public SimpleListProperty<MessageDAO> messagesProperty() {
        return messages;
    }

    public void sendMessage() {

        if (getMessage().isBlank()) {
            return;
        }

        MessageDAO messageDAO = new MessageDAO(getSender(), getReceiver(), getMessage(), LocalDateTime.now());

        CompletableFuture.runAsync(() -> ChatController.addMessageToChat(messageDAO));
        CompletableFuture.runAsync(() -> {
            ChatRequest data = converter.toChatRequest(messageDAO);
            model.send(data);
        });
    }

    public List<MessageDAO> loadChatHistory(int offset, int limit) {

        ChatRequest historyRequest = converter.toChatHistoryRequest(getSender(), getReceiver());
        return model.getChatHistory(historyRequest, offset, limit);
    }

    public static void setUpChatConnection() {

        if (WebSocketClientHolder.getInstance().isConnected()) {
            return;
        }

        String userId = UserSession.getInstance().getLoggedInUser().getId().toString();
        Map<String, String> gWSData = Infrastructure.getGatewayChatWSData();
        String params = DataParser.paramsString(Map.of("userId", userId));
        String spec = Infrastructure.uriSpecificPart(gWSData, "chat", params);

        URI baseUri = Infrastructure.getWSBaseUri(gWSData).resolve(spec);
        WebSocketClientHolder.getInstance().connect(baseUri, new ChatWebSocketListener());
    }
}
