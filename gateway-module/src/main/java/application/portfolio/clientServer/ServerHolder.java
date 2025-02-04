package application.portfolio.clientServer;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class ServerHolder {

    private static final HttpServer server;

    static {
        try {
            server = HttpServer.create();
        } catch (IOException e) {
            throw new RuntimeException("Error creating server: ", e);
        }
    }

    private ServerHolder() {}

    public static HttpServer getServer() {
        return server;
    }

    public static void bind(String host, int port, int backLog) {
        try {
            server.bind(new InetSocketAddress(host, port), backLog);
        } catch (IOException e) {
            throw new RuntimeException("Error Binding Inet Socket: ", e);
        }
    }

    public static void start() {
        server.start();
    }
}
