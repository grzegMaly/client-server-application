package application.portfolio.endpoints.endpointClasses.chat.chatUtils;

import application.portfolio.clientServer.DBConnectionHolder;
import application.portfolio.clientServer.response.MessageResponse;
import application.portfolio.objects.model.Message.Message;
import application.portfolio.objects.model.Message.MessageUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;

public class ChatPostMethods {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static MessageResponse sendMessage(byte[] data) throws IOException {

        JsonNode node = objectMapper.readTree(data);
        if (!validateMessageNode(node)) {
            return new MessageResponse("Bad Data", HTTP_FORBIDDEN);
        }

        Message message = MessageUtils.createMessage(node);
        if (message == null) {
            return new MessageResponse("Bad Data", HTTP_FORBIDDEN);
        }

        Connection conn = DBConnectionHolder.getConnection();
        try (CallableStatement cs = conn.prepareCall(
                DBConnectionHolder.saveMessage()
        )) {

            cs.setObject(1, message.getSenderId());
            cs.setObject(2, message.getReceiverId());
            cs.setString(3, message.getContent());
            cs.setObject(4, message.getTimestamp());

            cs.registerOutParameter(5, Types.INTEGER);
            return new MessageResponse()
                    .messageResponseFromDB(cs, 5);
        } catch (SQLException e) {
            return new MessageResponse("Unknown Error", HTTP_INTERNAL_ERROR);
        }
    }

    private static boolean validateMessageNode(JsonNode node) {
        return node.hasNonNull("senderId") && node.hasNonNull("receiverId") &&
                node.hasNonNull("content") && node.hasNonNull("timestamp");
    }
}
