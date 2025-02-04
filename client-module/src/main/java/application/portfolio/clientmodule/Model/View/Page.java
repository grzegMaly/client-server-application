package application.portfolio.clientmodule.Model.View;


import application.portfolio.clientmodule.TeamLinkApp;
import application.portfolio.clientmodule.utils.ExecutorServiceManager;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

public interface Page {

    default Boolean createScene() {
        try {
            Scene scene = new Scene(asParent());
            TeamLinkApp.addScene(this.getClass(), scene);
            return true;
        } catch (Exception e) {
            System.out.println("Exception in createScene loadPage");
            return false;
        }
    }

    default CompletableFuture<Boolean> loadPage() {
        return loadPage(ExecutorServiceManager.createCachedThreadPool(asParent().getClass().getSimpleName()));
    }

    default CompletableFuture<Boolean> loadPage(ExecutorService executor) {

        CompletableFuture<Boolean> initializePage = initializePage();
        CompletableFuture<Boolean> loadStyleClassFuture = CompletableFuture.supplyAsync(this::loadStyleClass, executor);

        return CompletableFuture.allOf(initializePage, loadStyleClassFuture)
                .thenCompose(v -> {
                    try {
                        boolean initialized = initializePage.get();
                        boolean stylesLoaded = loadStyleClassFuture.get();

                        if (initialized && stylesLoaded) {
                            loadStyles();
                            return CompletableFuture.completedFuture(true);
                        }
                    } catch (ExecutionException | InterruptedException e) {
                        System.out.println("Exception in loadPage: " + e.getMessage());
                        return CompletableFuture.completedFuture(false);
                    }

                    return CompletableFuture.completedFuture(false);
                }).exceptionally(e -> {
                    /*System.out.println("Exception in loadPage: " + e.getMessage());
                    return false;*/
                    throw new RuntimeException(e);
                });
    }

    default CompletableFuture<Boolean> initializePage() {
        return CompletableFuture.completedFuture(true);
    }

    default void bindSizeProperties() {
    }

    default Boolean loadStyleClass() {
        return true;
    }

    default void loadStyles() {}

    Parent asParent();

    default AtomicBoolean usedAsScene() {
        return new AtomicBoolean(false);
    }
}