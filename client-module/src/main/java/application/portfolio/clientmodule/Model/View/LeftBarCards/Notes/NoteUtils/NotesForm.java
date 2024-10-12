package application.portfolio.clientmodule.Model.View.LeftBarCards.Notes.NoteUtils;

import application.portfolio.clientmodule.Model.View.Page;
import application.portfolio.clientmodule.utils.ExecutorServiceManager;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class NotesForm extends GridPane implements Page {

    private final ButtonBar btnBar = new ButtonBar();
    private final Button listBtn = new Button("Notes List");
    private final Button saveBtn = new Button("Save");

    private final Label titleLbl = new Label("Title:");
    private final TextField titleTf = new TextField();
    private final Label noteTypeLbl = new Label("Note Type:");
    private final Label deadlineLbl = new Label("Deadline:");

    private final StackPane checkBoxes = new StackPane();
    private final ComboBox<String> typeCb = new ComboBox<>();
    private final ComboBox<String> categoryCb = new ComboBox<>();
    private final ComboBox<String> priorityCb = new ComboBox<>();

    private final DatePicker datePicker = new DatePicker();
    private final TextArea contentTa = new TextArea();

    private final ExecutorService executor =
            ExecutorServiceManager.createCachedThreadPool(this.getClass().getSimpleName());
    private final NoteBinder noteBinder = new NoteBinder();
    private final NotesFormAction actions = new NotesFormAction();

    private NotesForm() {
    }

    @Override
    public CompletableFuture<Boolean> initializePage() {

        CompletableFuture<Boolean> checkBoxesFuture = initializeCheckBoxes();
        CompletableFuture<Void> datePickerFuture =
                CompletableFuture.runAsync(() -> noteBinder.withNoteDeadlineDp(datePicker), executor);

        return CompletableFuture.allOf(checkBoxesFuture, datePickerFuture)
                .thenCompose(ignored -> checkBoxesFuture)
                .thenApply(success -> {
                    if (success) {
                        Platform.runLater(this::completeGridPane);
                    }
                    return CompletableFuture.completedFuture(success);
                })
                .thenRunAsync(this::setSaveBtnAction, executor)
                .thenRun(actions::useListener)
                .thenApply(v -> true)
                .exceptionally(e -> {
                    /*System.out.println("initializePage " + this.getClass().getSimpleName());
                    e.printStackTrace();
                    return false;*/
                    throw new RuntimeException(e);
                });
    }

    public void setSwitchVisibility(NotesList notesList) {
        listBtn.setOnAction(evt -> {
            this.setVisible(false);
            notesList.setVisible(true);
        });
    }

    private void setSaveBtnAction() {
        saveBtn.setOnAction(evt -> {
             noteBinder.save();
        });
    }

    private CompletableFuture<Boolean> initializeCheckBoxes() {

        CompletableFuture<Void> noteTypeFuture = CompletableFuture.runAsync(
                () -> {
                    noteBinder.withNoteTypeCb(typeCb);
                    actions.setTypeCb(typeCb);
                }, executor);
        CompletableFuture<Void> categoryFuture = CompletableFuture.runAsync(
                () -> {
                    noteBinder.withNoteCategoryCb(categoryCb);
                    actions.setCategoryCb(categoryCb);
                }, executor);
        CompletableFuture<Void> priorityFuture = CompletableFuture.runAsync(
                () -> noteBinder.withNotePriorityCb(priorityCb), executor);

        return CompletableFuture.allOf(noteTypeFuture, categoryFuture, priorityFuture)
                .thenRun(() -> {
                            Platform.runLater(() ->
                                    checkBoxes.getChildren().addAll(priorityCb, categoryCb));
                            actions.setChangingCheckBoxes(checkBoxes);
                        }
                )
                .thenApply(v -> true)
                .exceptionally(e -> {
                    System.out.println("initializeCheckBoxes " + this.getClass().getSimpleName());
                    e.printStackTrace();
                    return false;
                });
    }

    private void completeGridPane() {

        CompletableFuture.runAsync(() -> {
            ColumnConstraints col0 = new ColumnConstraints();
            ColumnConstraints col1 = new ColumnConstraints();
            ColumnConstraints col2 = new ColumnConstraints();
            ColumnConstraints col3 = new ColumnConstraints();
            ColumnConstraints col4 = new ColumnConstraints();

            col0.setPercentWidth(25);
            col1.setPercentWidth(20);
            col2.setPercentWidth(20);
            col3.setPercentWidth(20);
            col4.setPercentWidth(15);

            RowConstraints row0 = new RowConstraints();
            RowConstraints row1 = new RowConstraints();
            RowConstraints row2 = new RowConstraints();
            RowConstraints row3 = new RowConstraints();
            RowConstraints row4 = new RowConstraints();
            RowConstraints row5 = new RowConstraints();

            row0.setPrefHeight(30);
            row1.setPrefHeight(30);
            row2.setPrefHeight(30);
            row3.setPrefHeight(30);
            row4.setPrefHeight(30);
            row5.setPrefHeight(350);

            this.getColumnConstraints().addAll(col0, col1, col2, col3, col4);
            this.getRowConstraints().addAll(row0, row1, row2, row3, row4, row5);

            Platform.runLater(() -> {

                completeBtnBar();
                completeTitle();
                completeBoxes();
                completeContent();
            });
        }, executor);
    }

    private void completeBtnBar() {

        btnBar.getButtons().addAll(listBtn, saveBtn);
        this.add(btnBar, 3, 0, 2, 1);
        GridPane.setHalignment(btnBar, HPos.RIGHT);
    }

    private void completeTitle() {

        noteBinder.withTitleTf(titleTf);
        this.add(titleLbl, 0, 2);
        this.add(titleTf, 1, 2);
        GridPane.setColumnSpan(titleTf, 2);
    }

    private void completeBoxes() {

        this.add(noteTypeLbl, 0, 3);
        this.add(typeCb, 1, 3);
        this.add(checkBoxes, 2, 3);

        this.add(deadlineLbl, 3, 2);
        this.add(datePicker, 3, 3);
        CompletableFuture.runAsync(() -> actions.setDeadlineLbl(deadlineLbl), executor);
        CompletableFuture.runAsync(() -> actions.setDatePicker(datePicker), executor);

        typeCb.setMaxWidth(Double.MAX_VALUE);
        categoryCb.setMaxWidth(Double.MAX_VALUE);
        priorityCb.setMaxWidth(Double.MAX_VALUE);

        GridPane.setHgrow(typeCb, Priority.ALWAYS);
        GridPane.setHgrow(categoryCb, Priority.ALWAYS);
        GridPane.setHgrow(priorityCb, Priority.ALWAYS);
        GridPane.setHalignment(datePicker, HPos.LEFT);
        GridPane.setValignment(datePicker, VPos.TOP);
    }

    private void completeContent() {

        noteBinder.withContent(contentTa);
        this.add(contentTa, 0, 5, 3, 1);
        contentTa.setMinHeight(350);
    }

    @Override
    public Parent asParent() {
        return this;
    }
}