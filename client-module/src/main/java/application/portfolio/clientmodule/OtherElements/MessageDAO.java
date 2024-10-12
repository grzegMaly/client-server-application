package application.portfolio.clientmodule.OtherElements;

import java.time.LocalDateTime;

public class MessageDAO {

    private PersonDAO sender;
    private PersonDAO receiver;
    private String content;
    private LocalDateTime timestamp;

    public MessageDAO(PersonDAO sender, PersonDAO receiver, String content, LocalDateTime timestamp) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.timestamp = timestamp;
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
                "senderId=" + sender +
                ", receiverId=" + receiver +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
