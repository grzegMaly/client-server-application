package application.portfolio;

import application.portfolio.clientServer.DBConnectionHolder;
import application.portfolio.clientServer.ServerHolder;
import application.portfolio.endpoints.EndpointHandler;
import application.portfolio.utils.Infrastructure;
import com.sun.net.httpserver.HttpServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class DatabaseServer {
    public static void main(String[] args) {
        if (!setServer()) {
            throw new RuntimeException("Error loading server");
        }

        if (!connectToDatabase()) {
            throw new RuntimeException("Error loading server");
        }

        if (!loadEndpoints()) {
            throw new RuntimeException("Error loading endpoints");
        }

         if (!DBConnectionHolder.setUpConnection()) {
             throw new RuntimeException("Cannot login to database");
         }

        ServerHolder.start();
    }

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

    private static boolean connectToDatabase() {

        return true;
    }

    private static boolean loadEndpoints() {

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        ServiceLoader<EndpointHandler> loader = ServiceLoader.load(EndpointHandler.class);
        HttpServer server = ServerHolder.getServer();

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
