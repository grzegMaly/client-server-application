package application.portfolio.clientmodule.Model.View;


import application.portfolio.clientmodule.TeamLinkApp;
import application.portfolio.clientmodule.utils.ExecutorServiceManager;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

public interface Page {

    default Boolean createScene() {
        try {
            Scene scene = new Scene(asParent());
            TeamLinkApp.addScene(this.getClass(), scene);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    default CompletableFuture<Boolean> loadPage() {
        return loadPage(ExecutorServiceManager.createCachedThreadPool(asParent().getClass().getSimpleName()));
    }

    default CompletableFuture<Boolean> loadPage(ExecutorService executor) {

        CompletableFuture<Boolean> initializePage = initializePage();
        CompletableFuture<Boolean> loadStyleClassFuture = CompletableFuture.supplyAsync(this::loadStyleClass, executor);

        return initializePage
                .thenCombine(loadStyleClassFuture, (initialSuccess, lSCSuccess) -> {
                    if (initialSuccess && lSCSuccess) {
                        loadStyles();
                        return true;
                    }
                    return false;
                }).exceptionally(e -> false);
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