package application.portfolio.clientServer;

import application.portfolio.message.PostMessageMethods;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class MyWebSocketServer extends WebSocketServer {

    private final InetSocketAddress[] connections = new InetSocketAddress[1];
    private final ObjectMapper objectMapper = new ObjectMapper();

    public MyWebSocketServer(String host, int port) {
        super(new InetSocketAddress(host, port));
        this.setMaxPendingConnections(1);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        if (connections[0] == null) {
            connections[0] = conn.getRemoteSocketAddress();
        } else {
            conn.close(4005, "Unauthorized");
        }
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        if (conn.getRemoteSocketAddress().equals(connections[0])) {
            connections[0] = null;
        }
        conn.close();
    }

    @Override
    public void onMessage(WebSocket conn, String message) {

        if (connections[0] != conn.getRemoteSocketAddress()) {
            return;
        }
        System.out.println(message);

        JsonNode node = parseMessage(message);
        if (node == null) {
            return;
        }

        if (!PostMessageMethods.postMessageToDataBase(message)) {
            message = generateErrorResponse(node, "Error");
            System.out.println(message);
        } else {
            message = prepareMessageForRecipient(node);
            if (message == null) {
                return;
            }
        }
        conn.send(message);
    }

    private String prepareMessageForRecipient(JsonNode node) {

        ((ObjectNode) node).remove("tempId");
        try {
            return objectMapper.writeValueAsString(node);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private String generateErrorResponse(JsonNode node, String message) {

        ObjectNode responseNode = objectMapper.createObjectNode();
        String receiverId = node.get("senderId").asText();
        String tempId = node.get("tempId").asText();

        responseNode.put("tempId", tempId);
        responseNode.put("senderId", receiverId);
        responseNode.put("receiverId", receiverId);
        responseNode.put("content", message);

        return responseNode.asText();
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("WebSocket error: " + (ex != null ? ex.getMessage() : "Unknown error"));
    }

    @Override
    public void onStart() {
    }

    private JsonNode parseMessage(String message) {

        JsonNode node;
        try {
            node = objectMapper.readTree(message);
        } catch (JsonProcessingException e) {
            return null;
        }

        if (node.hasNonNull("senderId") && node.hasNonNull("tempId")) {
            return node;
        }
        return null;
    }
}
