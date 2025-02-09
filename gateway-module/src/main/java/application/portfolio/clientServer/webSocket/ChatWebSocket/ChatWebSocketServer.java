package application.portfolio.clientServer.webSocket.ChatWebSocket;

import application.portfolio.clientServer.webSocket.WebSocketPair;
import application.portfolio.clientServer.webSocket.WebSocketResourceInfo;
import application.portfolio.clientServer.webSocket.WebSocketServerModule;
import application.portfolio.utils.DataParser;

import application.portfolio.utils.Infrastructure;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@WebSocketResourceInfo(currentResource = "currentChatWebSocketServer")
public class ChatWebSocketServer extends WebSocketServer implements WebSocketServerModule {

    private final ConcurrentHashMap<InetSocketAddress, UUID> activeConnections = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private ChatWebSocketClient client;

    public ChatWebSocketServer() {
    }

    private ChatWebSocketServer(InetSocketAddress inetSocketAddress) {
        super(inetSocketAddress);
    }

    @Override
    public WebSocketPair initialize(Map<String, String> data) {

        String host = Infrastructure.getHost(data);
        int port = Integer.parseInt(Infrastructure.getPort(data));

        ChatWebSocketServer server = new ChatWebSocketServer(new InetSocketAddress(host, port));

        Map<String, String> clientMap = Infrastructure.getData("chatWS", 1);
        URI baseUri = Infrastructure.getWBBaseUri(clientMap);
        ChatWebSocketClient webSocketClient = new ChatWebSocketClient(baseUri, server);
        server.setWebSocketClient(webSocketClient);
        try {
            webSocketClient.connectBlocking();
        } catch (InterruptedException e) {
            return null;
        }
        return new WebSocketPair(server, webSocketClient);
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {

        String requestURI = webSocket.getResourceDescriptor();
        String[] chatUri = requestURI.split("\\?");
        var params = DataParser.getParams(chatUri[1]);

        if (!validateChatEndpoint(webSocket, chatUri, params, "userId")) {
            return;
        }

        String id = params.get("userId");
        UUID userId = DataParser.parseId(id);
        if (activeConnections.containsValue(userId)) {
            webSocket.close(4001, "User already connected");
            return;
        }
        activeConnections.put(webSocket.getRemoteSocketAddress(), userId);
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {

        activeConnections.remove(webSocket.getRemoteSocketAddress());
        webSocket.close();
    }

    @Override
    public void onMessage(WebSocket webSocket, String message) {

        String requestURI = webSocket.getResourceDescriptor();
        String[] chatUri = requestURI.split("\\?");
        var params = DataParser.getParams(chatUri[1]);

        if (!validateChatEndpoint(webSocket, chatUri, params, "userId")) {
            return;
        }

        UUID userId = activeConnections.get(webSocket.getRemoteSocketAddress());
        if (userId == null) {
            webSocket.close(40002, "Unauthorized");
            return;
        }
        forwardMessageToWebSocketServer(message);
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
    }

    @Override
    public void onStart() {
    }

    private void forwardMessageToWebSocketServer(String message) {
        client.send(message);
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    public void sendMessageToClient(String message) {

        JsonNode node;
        try {
            node = objectMapper.readTree(message);
        } catch (JsonProcessingException e) {
            return;
        }

        if (!node.hasNonNull("receiverId")) {
            return;
        }
        String id = node.get("receiverId").asText();
        UUID receiverId = DataParser.parseId(id);

        activeConnections.entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(receiverId))
                .map(Map.Entry::getKey)
                .findFirst()
                .flatMap(address -> getConnections().stream()
                        .filter(conn -> conn.getRemoteSocketAddress().equals(address))
                        .findFirst())
                .ifPresent(receiverWebSocket -> receiverWebSocket.send(message));
    }

    public void setWebSocketClient(ChatWebSocketClient client) {
        this.client = client;
    }

    private boolean validateChatEndpoint(WebSocket webSocket, String[] chatUri,
                                         Map<String, String> params, String param) {

        if (!chatUri[0].equals("/chat") || chatUri.length != 2) {
            webSocket.close(4000, "Invalid URI");
            return false;
        }

        if (params == null || !params.containsKey(param)) {
            webSocket.close(4001, "userId not found");
            return false;
        }
        return true;
    }
}
