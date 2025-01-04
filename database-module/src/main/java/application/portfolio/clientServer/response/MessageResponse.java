package application.portfolio.clientServer.response;

import application.portfolio.objects.dao.Message.MessageDAO;
import application.portfolio.objects.model.Message.Message;
import application.portfolio.objects.model.Message.MessageUtils;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;

public class MessageResponse extends Response<Message, MessageDAO> {

    public MessageResponse() {
    }

    public MessageResponse(String message, int statusCode) {
        super(message, statusCode);
    }

    public MessageResponse messageResponseFromDB(CallableStatement cs, Integer outputPosition)
            throws SQLException {

        Response<Message, MessageDAO> response = executeCallable(cs, outputPosition);
        CallableStatement csR = response.getCallableStatement();
        if (csR == null) {
            return new MessageResponse(response.getMessage(), response.getStatusCode());
        }

        try (csR) {
            ResultSet rs = csR.getResultSet();
            handleResultSet(rs);
        }
        return this;
    }

    private void handleResultSet(ResultSet rs) throws SQLException {

        int cCount = rs.getMetaData().getColumnCount();

        if (cCount == 1) {
            rs.next();
            String message = rs.getString(1);
            setMessage(message);
            setStatusCode(HTTP_OK);
            if (!message.equalsIgnoreCase("Chat Created")) {
                setStatusCode(HTTP_UNAUTHORIZED);
            }
        } else if (cCount > 1) {
            List<Message> messages = new ArrayList<>();
            while (rs.next()) {
                Message message = MessageUtils.createMessage(rs);
                if (message != null) {
                    messages.add(message);
                }
            }
            setItems(messages);
            setStatusCode(HTTP_OK);
        }
    }
}
