package application.portfolio.clientmodule.Model.Request.Chat.Chat;

import application.portfolio.clientmodule.Model.Request.Chat.Chat.ChatRequest.ChatRequest;
import application.portfolio.clientmodule.Model.Model.Chat.MessageDAO;
import application.portfolio.clientmodule.Model.Model.Person.Person;
import application.portfolio.clientmodule.utils.DataParser;

import java.util.Map;

public class ChatRequestConverter {
    public ChatRequest toChatRequest(MessageDAO message) {
        return new ChatRequest(
                message.getTempId(),
                message.getSender().getUserId(),
                message.getReceiver().getUserId(),
                message.getContent(),
                message.getTimestamp().toString()
        );
    }

    public ChatRequest toChatHistoryRequest(Person sender, Person receiver) {
        return new ChatRequest(sender.getUserId(), receiver.getUserId());
    }

    public static String toQueryParams(ChatRequest request, int offset, int limit) {
        String sId = request.getSenderId().toString();
        String rId = request.getReceiverId().toString();
        return DataParser.paramsString(Map.of("senderId", sId,
                "receiverId", rId,
                "offset", Integer.toString(offset),
                "limit", Integer.toString(limit)));
    }
}