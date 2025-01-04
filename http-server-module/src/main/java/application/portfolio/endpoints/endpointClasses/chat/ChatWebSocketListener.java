package application.portfolio.endpoints.endpointClasses.chat;

import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;

public class ChatWebSocketListener implements WebSocket.Listener {

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        return WebSocket.Listener.super.onText(webSocket, data, last);
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        WebSocket.Listener.super.onError(webSocket, error);
    }
}
