package application.portfolio.objects.model.Message;

import application.portfolio.objects.dao.Message.MessageDAO;
import application.portfolio.objects.dao.DAOConverter;

import java.time.LocalDateTime;
import java.util.UUID;

public class Message implements DAOConverter<Message, MessageDAO> {

    private long messageId;
    private UUID senderId;
    private UUID receiverId;
    private String content;
    private LocalDateTime timestamp;

    public Message() {
    }

    public Message(UUID senderId, UUID receiverId, String content, LocalDateTime timestamp) {
        this(-1, senderId, receiverId, content, timestamp);
    }

    public Message(long messageId, UUID senderId, UUID receiverId,
                   String content, LocalDateTime timestamp) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = timestamp;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public void setSenderId(UUID senderId) {
        this.senderId = senderId;
    }

    public UUID getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(UUID receiverId) {
        this.receiverId = receiverId;
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
    public MessageDAO toDAO() {
        return new MessageDAO(getMessageId(), getSenderId(), getReceiverId(),
                getContent(), getTimestamp().toString());
    }
}
