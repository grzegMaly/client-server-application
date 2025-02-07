package application.portfolio.clientmodule.Model.View.LeftBarCards.Notes.NoteUtils;

import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class NotesForm extends GridPane {

    private final ButtonBar btnBar = new ButtonBar();
    private final Button listBtn = new Button("Notes List");
    private Button saveBtn;

    private final Label titleLbl = new Label("Title:");
    private final TextField titleTf = new TextField();
    private final Label noteTypeLbl = new Label("Note Type:");
    private final Label deadlineLbl = new Label("Deadline:");

    private final StackPane checkBoxes = new StackPane();
    private final ComboBox<String> typeCb = new ComboBox<>();
    private final ComboBox<String> categoryCb = new ComboBox<>();
    private final ComboBox<String> priorityCb = new ComboBox<>();

    private DatePicker datePicker;
    private final TextArea contentTa = new TextArea();

    private ExecutorService executor;
    private final NotesFormAction actions = new NotesFormAction();

    private NoteBinder noteBinder;

    public NotesForm() {
    }

    public CompletableFuture<Boolean> initPage(NoteBinder noteBinder, ExecutorService executor) {

        this.noteBinder = noteBinder;
        this.executor = executor;

        CompletableFuture<Boolean> checkBoxesFuture = initializeCheckBoxes();
        CompletableFuture<Boolean> datePickerFuture = CompletableFuture.supplyAsync(this::setUpDatePicker, executor);

        return checkBoxesFuture.thenCombineAsync(datePickerFuture, (boxesSuccess, pickerSuccess) -> {
            if (!boxesSuccess || !pickerSuccess) {
                return false;
            }
            Platform.runLater(this::completeGridPane);
            return true;
        }, executor).thenApply(success -> {
            if (!success) return false;

            setSaveBtnAction();
            actions.useListener();
            return true;
        }).exceptionally(ext -> {
            System.err.println("Błąd inicjalizacji: " + ext.getMessage());
            return false;
        });
    }

    private boolean setUpDatePicker() {
        datePicker = noteBinder.withNoteDeadlineDp();
        return datePicker != null;
    }

    public void setSwitchVisibility(NotesList notesList) {
        listBtn.setOnAction(evt -> {
            this.setVisible(false);
            notesList.setVisible(true);
        });
    }

    private void setSaveBtnAction() {
        saveBtn = noteBinder.bindSaveBtn();
    }

    private CompletableFuture<Boolean> initializeCheckBoxes() {

        CompletableFuture<Boolean> noteTypeFuture = CompletableFuture.supplyAsync(() -> {
                    noteBinder.withNoteTypeCb(typeCb);
                    actions.setTypeCb(typeCb);
                    return true;
                }, executor).exceptionally(ext -> false);
        CompletableFuture<Boolean> categoryFuture = CompletableFuture.supplyAsync(() -> {
                    noteBinder.withNoteCategoryCb(categoryCb);
                    actions.setCategoryCb(categoryCb);
                    return true;
                }, executor).exceptionally(ext -> false);
        CompletableFuture<Boolean> priorityFuture = CompletableFuture.supplyAsync(() -> {
                    noteBinder.withNotePriorityCb(priorityCb);
                    return true;
                }, executor).exceptionally(ext -> false);

        return noteTypeFuture.thenCombineAsync(categoryFuture, (typeSuccess, categorySuccess) ->
                typeSuccess && categorySuccess, executor)
                .thenCombineAsync(priorityFuture, (prevSuccess, prioritySuccess) ->
                        prevSuccess && prioritySuccess, executor)
                .thenCompose(success -> {
                    if (!success) return CompletableFuture.completedFuture(false);

                    return CompletableFuture.runAsync(() -> {
                        Platform.runLater(() -> checkBoxes.getChildren().addAll(priorityCb, categoryCb));
                        actions.setChangingCheckBoxes(checkBoxes);
                    }).thenApply(v -> true);
                })
                .exceptionally(e -> {
                    System.err.println("Błąd w initializeCheckBoxes: " + this.getClass().getSimpleName() + " -> " + e.getMessage());
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
}