package application.portfolio.objects.dao.Message;

import java.util.UUID;

public class MessageDAO {

    private long id;
    private UUID senderId;
    private UUID receiverId;
    private String content;
    private String timestamp;

    public MessageDAO() {
    }

    public MessageDAO(long messageId, UUID senderId, UUID receiverId,
                      String content, String timestamp) {
        this.id = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
