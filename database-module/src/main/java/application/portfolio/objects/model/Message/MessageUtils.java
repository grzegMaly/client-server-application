package application.portfolio.objects.model.Message;

import application.portfolio.utils.DataParser;
import com.fasterxml.jackson.databind.JsonNode;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.UUID;

public class MessageUtils {

    private static final DateTimeFormatter formatter;

    static {
        formatter = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd HH:mm:ss")
                .optionalStart()
                .appendFraction(ChronoField.MILLI_OF_SECOND, 1, 3, true)
                .optionalEnd()
                .toFormatter();
    }

    public static Message createMessage(ResultSet rs) {
        try {

            long noteId = rs.getLong(1);
            String senderId = rs.getString(2);
            String receiverId = rs.getString(3);
            String content = rs.getString(4);
            String timestamp = rs.getString(5);

            UUID suId = DataParser.parseId(senderId);
            UUID ruId = DataParser.parseId(receiverId);
            LocalDateTime lTimestamp = LocalDateTime.parse(timestamp, formatter);

            return new Message(noteId, suId, ruId, content, lTimestamp);
        } catch (Exception e) {
            return null;
        }
    }

    public static Message createMessage(JsonNode node) {
        try {

            String sId = node.get("senderId").asText();
            String rid = node.get("receiverId").asText();
            String content = node.get("content").asText();
            String timestamp = node.get("timestamp").asText();

            UUID sUId = DataParser.parseId(sId);
            UUID rUId = DataParser.parseId(rid);
            LocalDateTime dateTime = LocalDateTime.parse(timestamp);

            return new Message(sUId, rUId, content, dateTime);
        } catch (Exception e) {
            return null;
        }
    }
}
