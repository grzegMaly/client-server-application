package application.portfolio.clientmodule.Model.Request.Chat;

import application.portfolio.clientmodule.Model.Request.Chat.ChatRequest.ChatRequest;
import application.portfolio.clientmodule.OtherElements.MessageDAO;

public class ChatRequestConverter {
    public ChatRequest toChatRequest(MessageDAO message) {
        return new ChatRequest(
                message.getSender().getId().toString(),
                message.getReceiver().getId().toString(),
                message.getContent(),
                message.getTimestamp().toString()
        );
    }
}
