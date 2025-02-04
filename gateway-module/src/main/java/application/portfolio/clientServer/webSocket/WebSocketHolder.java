package application.portfolio.clientServer.webSocket;

import application.portfolio.utils.Infrastructure;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

public class WebSocketHolder {

    private static final Map<String, WebSocketPair> servers = new ConcurrentHashMap<>();

    private WebSocketHolder() {}

    public static boolean initializeServers() {

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        ServiceLoader<WebSocketServerModule> loader = ServiceLoader.load(WebSocketServerModule.class);

        for (WebSocketServerModule server : loader) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {

                WebSocketResourceInfo info = server.getClass().getAnnotation(WebSocketResourceInfo.class);
                String currentResource = info.currentResource();

                Map<String, String> curResourceMap = Infrastructure.getData(currentResource, 1);
                WebSocketPair pair = server.initialize(curResourceMap);
                System.out.println(pair.client() != null);
                servers.put(server.getName(), pair);
            });
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

    public static void start() {
        servers.values().forEach(v -> v.server().start());
    }
}
