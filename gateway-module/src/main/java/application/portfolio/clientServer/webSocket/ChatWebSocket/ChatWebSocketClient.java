package application.portfolio.clientServer.webSocket.ChatWebSocket;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class ChatWebSocketClient extends WebSocketClient {

    private final ChatWebSocketServer server;


    public ChatWebSocketClient(URI serverUri, ChatWebSocketServer server) {
        super(serverUri);
        this.server = server;
    }

    @Override
    public void onOpen(ServerHandshake handshakeData) {
        System.out.println("Made a connection");
    }

    @Override
    public void onMessage(String message) {
        server.sendMessageToClient(message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        /*System.err.println("WebSocket closed. Reason: " + reason);
        reconnect();*/
    }

    public void reconnect() {
        try {
            this.reconnectBlocking();
            System.out.println("Reconnected WebSocket client.");
        } catch (InterruptedException e) {
            System.err.println("Failed to reconnect WebSocket client: " + e.getMessage());
        }
    }

    @Override
    public void onError(Exception ex) {
        /*System.err.println("WebSocket error: " + (ex != null ? ex.getMessage() : "Unknown error"));

        // Użyj osobnego wątku do ponownego połączenia
        new Thread(() -> {
            try {
                reconnectBlocking();
                System.out.println("WebSocket reconnected.");
            } catch (InterruptedException e) {
                System.err.println("Failed to reconnect WebSocket: " + e.getMessage());
            }
        }).start();*/
    }
}
