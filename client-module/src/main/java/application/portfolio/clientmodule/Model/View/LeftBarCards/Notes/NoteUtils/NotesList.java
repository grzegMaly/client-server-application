package application.portfolio.clientmodule.Model.View.LeftBarCards.Notes.NoteUtils;

import application.portfolio.clientmodule.Model.Model.Notes.Note;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class NotesList extends VBox {

    protected final Button createNoteBtn = new Button("Create Note");
    protected Button reloadBtn;
    protected final ButtonBar btnBar = new ButtonBar();

    private NoteBinder noteBinder;
    private TableView<Note> notesTbl = null;

    public CompletableFuture<Boolean> initPage(NoteBinder noteBinder, ExecutorService executor) {

        this.noteBinder = noteBinder;

        CompletableFuture<Boolean> reloadFuture = CompletableFuture.supplyAsync(this::setUpReloadBtn, executor);
        CompletableFuture<Boolean> tableFuture = CompletableFuture.supplyAsync(this::setUpTable, executor);
        CompletableFuture<List<Note>> notesFuture = CompletableFuture.supplyAsync(noteBinder::loadNotes);

        return reloadFuture.thenCombineAsync(tableFuture, (reloadSuccess, tableSuccess) ->
                        reloadSuccess && tableSuccess, executor)
                .thenCombineAsync(notesFuture, (prevSuccess, notes) -> {

                    if (!prevSuccess) {
                        return false;
                    }

                    Platform.runLater(() -> {
                        btnBar.getButtons().addAll(createNoteBtn, reloadBtn);
                        this.getChildren().addAll(btnBar, notesTbl);
                        notesTbl.getItems().addAll(notes);
                    });
                    return true;
                }, executor)
                .exceptionally(ex -> {
                    System.err.println("Błąd inicjalizacji strony: " + ex.getMessage());
                    return false;
                });
    }

    private boolean setUpTable() {
        notesTbl = noteBinder.loadNotesView();
        return notesTbl != null;
    }

    private boolean setUpReloadBtn() {
        reloadBtn = noteBinder.bindReloadBtn();
        return reloadBtn != null;
    }

    public void setSwitchVisibility(NotesForm notesForm) {
        createNoteBtn.setOnAction(evt -> {
            this.setVisible(false);
            notesForm.setVisible(true);
        });
    }
}