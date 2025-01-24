package application.portfolio;

import application.portfolio.clientServer.ServerHolder;
import application.portfolio.endpoints.EndpointHandler;
import application.portfolio.utils.FilesManager;
import application.portfolio.utils.Infrastructure;
import com.sun.net.httpserver.HttpServer;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class FileServer {
    public static void main(String[] args) {

        if (!FilesManager.validateConfigFiles()) {
            throw new RuntimeException("Problems on loading files");
        }
        if (!setServer()) {
            throw new RuntimeException("Error loading server");
        }

        if (!loadEndpoints()) {
            throw new RuntimeException("Error loading endpoints");
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
        }catch (InterruptedException | ExecutionException e) {
            return false;
        }
    }
}
