package application.portfolio.clientmodule.Model.Request.Chat.Chat.ChatRequest;

import java.util.UUID;

public class ChatRequest {

    private UUID tempId;
    private UUID senderId;
    private UUID receiverId;
    private String content;
    private String timestamp;

    public ChatRequest(UUID senderId, UUID receiver) {
        this.senderId = senderId;
        this.receiverId = receiver;
    }

    public ChatRequest(UUID tempId, UUID senderId, UUID receiverId, String messageContent, String timestamp) {
        this.tempId = tempId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = messageContent;
        this.timestamp = timestamp;
    }

    public UUID getTempId() {
        return tempId;
    }

    public void setTempId(UUID tempId) {
        this.tempId = tempId;
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
