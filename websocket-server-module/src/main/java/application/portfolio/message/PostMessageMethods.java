package application.portfolio.message;

import application.portfolio.clientServer.ClientHolder;
import application.portfolio.utils.Infrastructure;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.util.Map;

public class PostMessageMethods {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static boolean postMessageToDataBase(String message) {

        JsonNode node;
        byte[] data;
        try {
            node = objectMapper.readTree(message);
            if (!validateMessage(node)) {
                return false;
            }
            data = objectMapper.writeValueAsBytes(node);
        } catch (JsonProcessingException e) {
            return false;
        }

        Map<String, String> dbData = Infrastructure.getDataBaseData();
        String spec = Infrastructure.uriSpecificPart(dbData, "chat");

        URI baseUri = Infrastructure.getBaseUri(dbData).resolve(spec);
        return ClientHolder.handlePostRequest(data, baseUri);
    }

    private static boolean validateMessage(JsonNode node) throws JsonProcessingException {
        return node.hasNonNull("senderId") &&
                node.hasNonNull("receiverId") &&
                node.hasNonNull("content") &&
                node.hasNonNull("timestamp");
    }
}
