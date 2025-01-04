package application.portfolio.clientServer;


import application.portfolio.utils.Infrastructure;
import org.java_websocket.server.WebSocketServer;

import java.util.Map;

public class WebSocketServerHolder {

    private static WebSocketServer server;

    private WebSocketServerHolder() {}

    public static boolean initializeServer() {
        try {
            Map<String, String> dData = Infrastructure.getCurrentWSServerData();
            String host = Infrastructure.getHost(dData);
            String port = Infrastructure.getPort(dData);

            server = new MyWebSocketServer(host, Integer.parseInt(port));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void start() {
        server.start();
    }
}
