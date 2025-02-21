package application.portfolio.clientmodule.Model.View.LeftBarCards.Notes;

import application.portfolio.clientmodule.Config.LoadStyles;
import application.portfolio.clientmodule.Model.View.LeftBarCards.Notes.NoteUtils.NoteBinder;
import application.portfolio.clientmodule.Model.View.LeftBarCards.Notes.NoteUtils.NotesForm;
import application.portfolio.clientmodule.Model.View.LeftBarCards.Notes.NoteUtils.NotesList;
import application.portfolio.clientmodule.Model.View.Page;
import application.portfolio.clientmodule.Model.View.Scenes.start.MainScene;
import application.portfolio.clientmodule.TeamLinkApp;
import application.portfolio.clientmodule.utils.ExecutorServiceManager;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.layout.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class Notes extends StackPane implements Page {

    private static NotesForm notesForm;
    private static NotesList notesList;
    private final NoteBinder noteBinder = new NoteBinder();
    private final NoteController noteController = new NoteController();

    private final ExecutorService executor =
            ExecutorServiceManager.createCachedThreadPool(this.getClass().getSimpleName());

    private Notes() {
    }

    @Override
    public CompletableFuture<Boolean> initializePage() {

        noteBinder.bindNoteController(noteController);
        return CompletableFuture.supplyAsync(this::initializeComponents, executor)
                .exceptionally(ext -> false);
    }

    private Boolean initializeComponents() {
        CompletableFuture<NotesForm> formFuture = CompletableFuture.supplyAsync(NotesForm::new, executor);

        return formFuture.thenCompose(form -> {
                    if (form == null) {
                        return CompletableFuture.completedFuture(false);
                    }

                    notesForm = form;
                    return notesForm.initPage(noteBinder, executor)
                            .thenApply(success -> {
                                if (!success) return false;

                                Platform.runLater(() -> this.getChildren().add(notesForm));

                                CompletableFuture.runAsync(() -> {
                                    notesList = new NotesList();
                                    notesList.initPage(noteBinder, executor)
                                            .exceptionally(ex -> {
                                                System.err.println("Błąd inicjalizacji NotesList: " + ex.getMessage());
                                                return false;
                                            })
                                            .thenAccept(successList -> {
                                                if (!successList) return;

                                                Platform.runLater(() -> {
                                                    notesList.setVisible(false);
                                                    this.getChildren().add(notesList);
                                                    notesForm.setSwitchVisibility(notesList);
                                                    notesList.setSwitchVisibility(notesForm);
                                                });
                                            });
                                }, executor);
                                return true;
                            });
                })
                .thenApply(success -> {
                    if (success) {
                        bindSizeProperties();
                    }
                    return success;
                }).exceptionally(ex -> {
                    System.err.println("Błąd inicjalizacji komponentów: " + ex.getMessage());
                    return false;
                }).join();
    }

    @Override
    public void bindSizeProperties() {

        Platform.runLater(() -> {
            notesForm.prefHeightProperty().bind(this.heightProperty());
            notesForm.prefWidthProperty().bind(this.widthProperty());

            notesList.prefHeightProperty().bind(this.heightProperty());
            notesForm.prefWidthProperty().bind(this.widthProperty());
        });
    }

    @Override
    public Boolean loadStyleClass() {
        return LoadStyles.loadNotesSceneStyle(TeamLinkApp.getScene(MainScene.class));
    }

    @Override
    public void loadStyles() {
        Platform.runLater(() -> Platform.runLater(() -> {
            notesForm.loadStyles();
            notesList.loadStyles();
        }));
    }

    @Override
    public Parent asParent() {
        return this;
    }
}