package application.portfolio;


import application.portfolio.endpoints.EndpointHandler;
import application.portfolio.utils.Infrastructure;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Main {

    private static HttpServer server;

    public static void main(String[] args) {

        if (!setServer()) {
            throw new RuntimeException("Error loading server");
        }

        if (!loadEndpoints()) {
            throw new RuntimeException("Error loading endpoints");
        }

        server.start();
    }

    private static boolean setServer() {

        Map<String, String> gatewayData = Infrastructure.getGatewayData();
        String host;
        int port;
        if (gatewayData.size() == 2) {
            host = gatewayData.get("host");
            port = Integer.parseInt(gatewayData.get("port"));
        } else {
            throw new RuntimeException("Error loading server data");
        }

        try {
            server = HttpServer.create(new InetSocketAddress(host, port), 0);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private static boolean loadEndpoints() {

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        ServiceLoader<EndpointHandler> loader = ServiceLoader.load(EndpointHandler.class);

        for (EndpointHandler handler : loader) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> handler.registerEndpoint(server));;
            futures.add(future);
        }

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        try {
            allOf.get();
            return true;
        } catch (InterruptedException | ExecutionException e) {
            return false;
        }
    }
}
