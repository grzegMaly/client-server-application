package application.portfolio.clientmodule.OtherElements;

import application.portfolio.clientmodule.Connection.UserSession;
import application.portfolio.clientmodule.Model.Request.Chat.Friends.FriendsRequestViewModel;
import application.portfolio.clientmodule.Model.View.LeftBarCards.Chat.ChatController;
import application.portfolio.clientmodule.utils.DataParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

public class MessageMethods {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final PersonDAO loggedInUsed = UserSession.getInstance().getLoggedInUser();

    public static MessageDAO createMessage(JsonNode node, PersonDAO sender, PersonDAO receiver)
            throws IllegalArgumentException {

        if (!validateNodeStructure(node)) {
            return null;
        }

        MessageDAO message;
        String senderId = node.get("senderId").asText();
        if (senderId.equalsIgnoreCase(sender.getId().toString())) {
            message = new MessageDAO(sender, receiver);
        } else {
            message = new MessageDAO(receiver, sender);
        }

        if (completeMessage(message, node)) {
            return message;
        }
        return null;
    }

    public static MessageDAO createMessage(JsonNode node) {

        if (!validateNodeStructure(node)) {
            return null;
        }

        PersonDAO receiver = UserSession.getInstance().getLoggedInUser();
        List<PersonDAO> friends = FriendsRequestViewModel.getFriends();

        String senderId = node.get("senderId").asText();
        PersonDAO sender = friends.stream()
                .filter(f -> f.getId().toString().equalsIgnoreCase(senderId))
                .findFirst()
                .orElse(null);

        if (sender == null) {
            return null;
        }

        MessageDAO message = new MessageDAO(sender, receiver);
        if (completeMessage(message, node)) {
            return message;
        }
        return null;
    }

    private static boolean completeMessage(MessageDAO message, JsonNode node) {
        long id = node.get("id").asLong();
        String content = node.get("content").asText();
        message.setId(id);
        message.setContent(content);

        try {
            LocalDateTime timestamp = LocalDateTime.parse(node.get("timestamp").asText());
            message.setTimestamp(timestamp);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static MessageDAO parseReceivedMessage(String data) {

        JsonNode node;
        PersonDAO actualUser = ChatController.getActualUser();
        try {
            node = objectMapper.readTree(data);
        } catch (JsonProcessingException e) {
            return null;
        }

        if (!validateNodeStructure(node)) {
            return null;
        }

        String id = node.get("senderId").asText();
        UUID senderId = DataParser.parseId(id);

        MessageDAO messageDAO;

        if (actualUser == null) {
            PersonDAO friend = FriendsRequestViewModel.getPerson(senderId);
            if (friend == null) {
                return null;
            } else {
                messageDAO = new MessageDAO(friend, loggedInUsed);
                messageDAO.setTempId(null);
            }
        } else {
            if (actualUser.getId().equals(senderId)) {
                messageDAO = new MessageDAO(actualUser, loggedInUsed);
                messageDAO.setTempId(null);
            } else if (loggedInUsed.getId().equals(senderId)) {
                if (!node.hasNonNull("tempId")) {
                    return null;
                }
                String tId = node.get("tempId").asText();
                UUID tempId = DataParser.parseId(tId);
                messageDAO = new MessageDAO(loggedInUsed, loggedInUsed);
                messageDAO.setTempId(tempId);
            } else {
                PersonDAO personDAO = FriendsRequestViewModel.getFriends()
                        .stream()
                        .filter(p -> p.getId().equals(senderId))
                        .findFirst()
                        .orElse(null);
                if (personDAO == null) {
                    return null;
                }

                messageDAO = new MessageDAO(personDAO, loggedInUsed);
                messageDAO.setTempId(null);
            }
        }

        String content = node.get("content").asText();
        String date = node.get("timestamp").asText();
        LocalDateTime timestamp;
        try {
            timestamp = LocalDateTime.parse(date);
        } catch (DateTimeParseException e) {
            return null;
        }

        messageDAO.setContent(content);
        messageDAO.setTimestamp(timestamp);
        return messageDAO;
    }

    private static boolean validateNodeStructure(JsonNode node) {
        return node.hasNonNull("senderId") && node.hasNonNull("receiverId") &&
                node.hasNonNull("content") && node.hasNonNull("timestamp");
    }
}
