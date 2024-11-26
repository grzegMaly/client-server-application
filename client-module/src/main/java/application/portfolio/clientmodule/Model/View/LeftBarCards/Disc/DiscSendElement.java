package application.portfolio.clientmodule.Model.View.LeftBarCards.Disc;

import application.portfolio.clientmodule.Model.View.Page;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class DiscSendElement extends VBox implements Page {

    @Override
    public CompletableFuture<Boolean> initializePage() {
        return Page.super.initializePage();
    }

    @Override
    public Parent asParent() {
        return this;
    }
}