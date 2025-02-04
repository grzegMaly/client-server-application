package application.portfolio.clientServer.webSocket;

import org.java_websocket.WebSocket;
import org.java_websocket.server.WebSocketServer;



public record WebSocketPair(WebSocketServer server, WebSocket client) {}
