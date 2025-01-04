package application.portfolio.clientServer.webSocket;

import java.util.Map;

public interface WebSocketServerModule {
    WebSocketPair initialize(Map<String, String> map);
    String getName();
}
