package application.portfolio;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import static java.net.HttpURLConnection.HTTP_OK;

public class Main {

    public static void main(String[] args) {

        try {

            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

            server.createContext("/", exchange -> {
                String request = new String(exchange.getRequestBody().readAllBytes());
                System.out.println("Client connected, request body: " + request);

                String response = "Siema";
                var bytes = response.getBytes();
                exchange.sendResponseHeaders(HTTP_OK, bytes.length);
                exchange.getResponseBody().write(bytes);
                exchange.close();
            });

            server.start();
            System.out.println("Server listening on port 8080");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
