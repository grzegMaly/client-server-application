package application.portfolio.clientmodule.Model.Model.Chat;

import application.portfolio.clientmodule.Model.Model.Person.PersonDAO;

import java.time.LocalDateTime;
import java.util.UUID;

public class MessageDAO {

    private UUID tempId = UUID.randomUUID();
    private Long id;
    private PersonDAO sender;
    private PersonDAO receiver;
    private String content;
    private LocalDateTime timestamp;

    public MessageDAO() {}

    public MessageDAO(PersonDAO sender, PersonDAO receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }

    public MessageDAO(PersonDAO sender, PersonDAO receiver, String content, LocalDateTime timestamp) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getTempId() {
        return tempId;
    }

    public void setTempId(UUID tempId) {
        this.tempId = tempId;
    }

    public PersonDAO getSender() {
        return sender;
    }

    public void setSender(PersonDAO sender) {
        this.sender = sender;
    }

    public PersonDAO getReceiver() {
        return receiver;
    }

    public void setReceiver(PersonDAO receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "MessageDAO{" +
                "tempId=" + tempId +
                ", id=" + id +
                ", sender=" + sender +
                ", receiver=" + receiver +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
