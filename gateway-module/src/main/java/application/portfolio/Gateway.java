package application.portfolio;


import application.portfolio.clientServer.webSocket.WebSocketHolder;
import application.portfolio.endpoints.EndpointHandler;
import application.portfolio.clientServer.ServerHolder;
import application.portfolio.utils.Infrastructure;
import com.sun.net.httpserver.HttpServer;

import java.util.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Gateway {

    public static void main(String[] args) {

        CompletableFuture<Boolean> httpServerFuture = CompletableFuture.supplyAsync(() -> {
            if (!setServer()) {
                throw new RuntimeException("Error loading server");
            }

            if (!loadEndpoints()) {
                throw new RuntimeException("Error loading endpoints");
            }

            ServerHolder.start();
            return true;
        }).exceptionally(ex -> false);


        CompletableFuture<Void> webSocketServerFuture = CompletableFuture.runAsync(() -> {
            if (!WebSocketHolder.initializeServers()) {
                throw new RuntimeException("Error loading WebSocket server");
            }
            WebSocketHolder.start();
        });

        CompletableFuture<Void> allOf = CompletableFuture.allOf(httpServerFuture, webSocketServerFuture);
        try {
            allOf.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to start Server");
        }

        //Todo: Implement in the future
//        new Thread(Gateway::tokenize).start();
    }

    /*private static void tokenize() {

        HttpClient client = ClientHolder.getClient();
        Map<String, Map<String, String>> serversData = Infrastructure.getInfrastructureData();
        int size = serversData.size();
        int count = 0;

        while (count < size) {
            Iterator<Map.Entry<String, Map<String, String>>> iterator = serversData.entrySet().iterator();
            while (iterator.hasNext()) {
                var element = iterator.next();
                String key = element.getKey();
                Map<String, String> value = element.getValue();

            }
        }
    }*/

    private static boolean setServer() {

        Map<String, String> data = Infrastructure.getCurrentServerData();
        String host;
        int port;
        if (data.size() == 2) {
            host = Infrastructure.getHost(data);
            port = Integer.parseInt(Infrastructure.getPort(data));
            ServerHolder.bind(host, port, 0);
            return true;
        } else {
            throw new RuntimeException("Error loading server data");
        }
    }

    private static boolean loadEndpoints() {

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        ServiceLoader<EndpointHandler> loader = ServiceLoader.load(EndpointHandler.class);
        HttpServer server = ServerHolder.getServer();

        for (EndpointHandler handler : loader) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> handler.registerEndpoint(server));
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