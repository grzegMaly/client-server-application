package application.portfolio.clientmodule.Model.View.LeftBarCards.Notes.NoteUtils;

import application.portfolio.clientmodule.Model.View.LeftBarCards.Notes.NoteController;
import application.portfolio.clientmodule.Model.View.Page;
import application.portfolio.clientmodule.Model.Model.Notes.NoteDAO;
import application.portfolio.clientmodule.utils.ExecutorServiceManager;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class NotesList extends VBox implements Page {

    protected final Button createNoteBtn = new Button("Create Note");
    protected final Button reloadBtn = new Button("Reload");
    protected final ButtonBar btnBar = new ButtonBar();

    private TableView<NoteDAO> notesTbl = null;

    private final ExecutorService executor =
            ExecutorServiceManager.createCachedThreadPool(this.getClass().getSimpleName());

    @Override
    public CompletableFuture<Boolean> initializePage() {

        return CompletableFuture.runAsync(() -> notesTbl = NoteController.getTableView(), executor)
                .thenRunAsync(() -> Platform.runLater(() -> {
                    btnBar.getButtons().addAll(createNoteBtn, reloadBtn);
                    this.getChildren().addAll(btnBar, notesTbl);
                }), executor)
                .thenApply(v -> true);
    }

    public void setSwitchVisibility(NotesForm notesForm) {
        createNoteBtn.setOnAction(evt -> {
            this.setVisible(false);
            notesForm.setVisible(true);
        });
    }

    @Override
    public Parent asParent() {
        return this;
    }
}