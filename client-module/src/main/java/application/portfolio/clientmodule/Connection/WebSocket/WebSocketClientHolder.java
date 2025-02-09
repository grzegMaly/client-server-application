package application.portfolio.clientmodule.Connection.WebSocket;

import application.portfolio.clientmodule.Connection.ClientHolder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;

public class WebSocketClientHolder {

    private static volatile WebSocketClientHolder instance;
    private WebSocket webSocket;
    private final HttpClient httpClient;

    private WebSocketClientHolder() {
        httpClient = ClientHolder.getClient();
    }

    public static WebSocketClientHolder getInstance() {
        if (instance == null) {
            synchronized (WebSocketClientHolder.class) {
                if (instance == null) {
                    instance = new WebSocketClientHolder();
                }
            }
        }
        return instance;
    }

    public void connect(URI uri, WebSocket.Listener listener) {

        if (webSocket != null) {
            return;
        }

        webSocket = httpClient.newWebSocketBuilder()
                .buildAsync(uri, listener)
                .join();
    }

    public WebSocket getWebSocket() {
        if (webSocket == null) {
            return null;
        }
        return webSocket;
    }

    public void close() {
        if (webSocket != null) {
            webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "Closing Connection")
                    .thenRun(() -> webSocket = null);
        }
    }

    public boolean isConnected() {
        return webSocket != null;
    }
}
