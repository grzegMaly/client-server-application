package application.portfolio.clientmodule.Model.View.LeftBarCards.Notes;

import application.portfolio.clientmodule.Model.View.LeftBarCards.Notes.NoteUtils.NotesForm;
import application.portfolio.clientmodule.Model.View.LeftBarCards.Notes.NoteUtils.NotesList;
import application.portfolio.clientmodule.Model.View.Page;
import application.portfolio.clientmodule.utils.ExecutorServiceManager;
import application.portfolio.clientmodule.utils.PageFactory;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.layout.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class Notes extends StackPane implements Page {

    private static NotesForm notesForm;
    private static NotesList notesList;

    private final ExecutorService executor =
            ExecutorServiceManager.createCachedThreadPool(this.getClass().getSimpleName());

    private Notes() {
    }

    @Override
    public CompletableFuture<Boolean> initializePage() {

        CompletableFuture<NotesForm> formFuture =
                CompletableFuture.supplyAsync(() -> PageFactory.getInstance(NotesForm.class), executor);
        CompletableFuture<NotesList> listFuture =
                CompletableFuture.supplyAsync(() -> PageFactory.getInstance(NotesList.class), executor);

        return formFuture.thenComposeAsync(form -> {
            if (form == null) {
                return CompletableFuture.completedFuture(false);
            }

            notesForm = form;
            Platform.runLater(() -> {
                notesForm.setVisible(true);
                this.getChildren().add(notesForm);
            });

            return listFuture.thenApplyAsync(list -> {
                if (list == null) {
                    return false;
                }

                notesList = list;
                Platform.runLater(() -> {
                    notesList.setVisible(false);
                    this.getChildren().add(notesList);
                });
                return true;
            });
        }).thenApplyAsync(result -> {
            if (result) {
                notesForm.setSwitchVisibility(notesList);
                notesList.setSwitchVisibility(notesForm);
            }
            return result;
        });
    }

    @Override
    public Parent asParent() {
        return this;
    }
}