package application.portfolio.clientmodule.utils;


import java.util.Map;
import java.util.concurrent.*;

public final class ExecutorServiceManager {

    private static final Map<String, ExecutorService> executors = new ConcurrentHashMap<>();

    public static ExecutorService createCachedThreadPool(String className) {
        return executors.computeIfAbsent(className, f -> Executors.newCachedThreadPool());
    }

    public static ExecutorService createSingleThreadExecutor(String className) {
        return executors.computeIfAbsent(className, f -> Executors.newSingleThreadExecutor());
    }

    public static void shutDownAll() {

        executors.keySet().forEach(ExecutorServiceManager::shutDownThis);
    }


    public static void shutDownThis(String key) {

        ExecutorService executor = executors.remove(key);
        shutDownThis(executor);
    }

    public static void shutDownThis(ExecutorService executor) {

        executor.shutdown();
        try {

            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException ie) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}