package application.portfolio.clientmodule.Model.Request.Chat.ChatRequest;

public class ChatRequest {

    private String senderId;
    private String receiverId;
    private String messageContent;
    private String timestamp;

    public ChatRequest(String senderId, String receiverId, String messageContent, String timestamp) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageContent = messageContent;
        this.timestamp = timestamp;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "ChatRequest{" +
                "senderId='" + senderId + '\'' +
                ", receiverId='" + receiverId + '\'' +
                ", messageContent='" + messageContent + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
