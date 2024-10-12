package application.portfolio.clientmodule.Model.Request.Chat;

import application.portfolio.clientmodule.Model.Request.Chat.ChatRequest.ChatRequest;
import application.portfolio.clientmodule.OtherElements.MessageDAO;
import application.portfolio.clientmodule.OtherElements.PersonDAO;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;

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

    public void save() {

        MessageDAO messageDAO = new MessageDAO(this.sender.get(), this.receiver.get(), this.getMessage(), LocalDateTime.now());

        ChatRequest data = converter.toChatRequest(messageDAO);
        model.save(data);
    }
}
