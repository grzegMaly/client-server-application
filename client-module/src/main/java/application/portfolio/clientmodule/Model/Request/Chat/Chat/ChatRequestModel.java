package application.portfolio.clientmodule.Model.Request.Chat.Chat;

import application.portfolio.clientmodule.Connection.ClientHolder;
import application.portfolio.clientmodule.Connection.Infrastructure;
import application.portfolio.clientmodule.Connection.UserSession;
import application.portfolio.clientmodule.Connection.WebSocket.WebSocketClientHolder;
import application.portfolio.clientmodule.Model.Request.Chat.Chat.ChatRequest.ChatRequest;
import application.portfolio.clientmodule.Model.Request.Chat.Friends.FriendsRequestViewModel;
import application.portfolio.clientmodule.OtherElements.MessageDAO;
import application.portfolio.clientmodule.OtherElements.MessageMethods;
import application.portfolio.clientmodule.OtherElements.PersonDAO;
import application.portfolio.clientmodule.utils.JsonBodyHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.WebSocket;
import java.time.Duration;
import java.util.*;

public class ChatRequestModel {

    private WebSocket webSocket;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void send(ChatRequest data) {

        if (webSocket == null) {
            webSocket = WebSocketClientHolder.getInstance().getWebSocket();
        }

        JsonNode node = objectMapper.valueToTree(data);
        String message = node.toString();
        webSocket.sendText(message, true);
    }

    public List<MessageDAO> getChatHistory(ChatRequest chatRequest, int offset, int limit) {

        String params = ChatRequestConverter.toQueryParams(chatRequest, offset, limit);
        Map<String, String> gData = Infrastructure.getGatewayData();
        String spec = Infrastructure.uriSpecificPart(gData, "chat/history", params);

        URI baseUri = Infrastructure.getBaseUri(gData).resolve(spec);

        HttpRequest request = HttpRequest.newBuilder(baseUri)
                .GET()
                .timeout(Duration.ofSeconds(10))
                .version(HttpClient.Version.HTTP_2)
                .header("Accept", "application/json")
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonBodyHandler handler = JsonBodyHandler.create(objectMapper);
        HttpResponse<JsonNode> response;
        try {
            response = ClientHolder.getClient().send(request, handler);
        } catch (IOException | InterruptedException e) {
            return Collections.emptyList();
        }

        if (response.statusCode() == 200) {
            return parseMessages(response, chatRequest);
        }
        return Collections.emptyList();
    }

    private List<MessageDAO> parseMessages(HttpResponse<JsonNode> response, ChatRequest chatRequest) {

        JsonNode node = response.body();
        node = node.get("response");

        UUID receiverId = chatRequest.getReceiverId();

        PersonDAO sender = UserSession.getInstance().getLoggedInUser();
        PersonDAO receiver = FriendsRequestViewModel.getPerson(receiverId);

        List<MessageDAO> messages = new ArrayList<>();

        if (node.isArray()) {
            try {
                System.out.println(node.size());
                for (JsonNode messageNode : node) {
                    MessageDAO message = MessageMethods.createMessage(messageNode, sender, receiver);
                    messages.add(message);
                }
            } catch (Exception e) {
                return Collections.emptyList();
            }
        } else if (node.isObject()) {
            MessageDAO messageDAO = MessageMethods.createMessage(node, sender, receiver);
            messages.add(messageDAO);
        } else {
            return Collections.emptyList();
        }
        return messages;
    }
}
