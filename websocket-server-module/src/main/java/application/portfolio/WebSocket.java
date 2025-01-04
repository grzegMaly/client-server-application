package application.portfolio;

import application.portfolio.clientServer.WebSocketServerHolder;

public class WebSocket {
    public static void main(String[] args) {

        if (!WebSocketServerHolder.initializeServer()) {
            throw new RuntimeException("Error loading WebSocket Server");
        }
        WebSocketServerHolder.start();
    }
}
