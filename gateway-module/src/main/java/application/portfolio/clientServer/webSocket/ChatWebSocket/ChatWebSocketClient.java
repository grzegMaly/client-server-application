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
    }

    @Override
    public void onMessage(String message) {
        server.sendMessageToClient(message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
    }

    public void reconnect() {
        try {
            this.reconnectBlocking();
        } catch (InterruptedException ignored) {
        }
    }

    @Override
    public void onError(Exception ex) {
    }
}
