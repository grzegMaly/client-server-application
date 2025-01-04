package application.portfolio.endpoints.endpointClasses.chat.chatUtils;

import application.portfolio.clientServer.DBConnectionHolder;
import application.portfolio.clientServer.response.MessageResponse;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;


public class ChatGetMethods {

    public static MessageResponse getChatHistoryFromDB(UUID senderId, UUID receiverId, int offset, int limit) {

        Connection conn = DBConnectionHolder.getConnection();
        try (CallableStatement cs = conn.prepareCall(
                DBConnectionHolder.getChatHistory()
        )) {

            cs.setObject(1, senderId);
            cs.setObject(2, receiverId);
            cs.setInt(3, offset);
            cs.setInt(4, limit);

            return new MessageResponse()
                    .messageResponseFromDB(cs, null);
        } catch (SQLException e) {
            return new MessageResponse("Unknown Error", HTTP_INTERNAL_ERROR);
        }
    }
}
