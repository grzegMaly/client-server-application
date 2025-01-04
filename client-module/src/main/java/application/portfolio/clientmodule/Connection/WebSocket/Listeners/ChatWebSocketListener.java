package application.portfolio.clientmodule.Connection.WebSocket.Listeners;

import application.portfolio.clientmodule.Model.View.LeftBarCards.Chat.ChatController;
import application.portfolio.clientmodule.OtherElements.MessageDAO;
import application.portfolio.clientmodule.OtherElements.MessageMethods;
import application.portfolio.clientmodule.utils.ExecutorServiceManager;

import java.net.http.WebSocket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;

public class ChatWebSocketListener implements WebSocket.Listener {

    private final ExecutorService executor =
            ExecutorServiceManager.createCachedThreadPool(this.getClass().getSimpleName());

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {

        CompletableFuture.runAsync(() -> handleMessage(data.toString()), executor);
        return WebSocket.Listener.super.onText(webSocket, data, last);
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        WebSocket.Listener.super.onError(webSocket, error);
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
    }

    private void handleMessage(String data) {

        MessageDAO messageDAO = MessageMethods.parseReceivedMessage(data);
        if (messageDAO == null) {
            return;
        }

        if (messageDAO.getTempId() != null) {
            ChatController.handleBadMessage(messageDAO);
        } else {
            ChatController.addMessageToChat(messageDAO);
        }
    }
}
