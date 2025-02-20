package application.portfolio.clientmodule.Model.Model.Chat;

import application.portfolio.clientmodule.Model.Model.Person.Person;

import java.time.LocalDateTime;
import java.util.UUID;

public class MessageDAO {

    private UUID tempId = UUID.randomUUID();
    private Long id;
    private Person sender;
    private Person receiver;
    private String content;
    private LocalDateTime timestamp;

    public MessageDAO() {}

    public MessageDAO(Person sender, Person receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }

    public MessageDAO(Person sender, Person receiver, String content, LocalDateTime timestamp) {
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

    public Person getSender() {
        return sender;
    }

    public void setSender(Person sender) {
        this.sender = sender;
    }

    public Person getReceiver() {
        return receiver;
    }

    public void setReceiver(Person receiver) {
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
}
