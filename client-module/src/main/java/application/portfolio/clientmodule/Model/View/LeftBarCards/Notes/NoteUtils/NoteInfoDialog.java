package application.portfolio.clientmodule.Model.View.LeftBarCards.Notes.NoteUtils;

import application.portfolio.clientmodule.Model.Model.Notes.Note;
import application.portfolio.clientmodule.Model.Model.Notes.NoteType;
import application.portfolio.clientmodule.utils.ExecutorServiceManager;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class NoteInfoDialog {

    private final Stage stage = new Stage(StageStyle.UTILITY);
    private final Scene scene;

    private final GridPane gp = new GridPane();

    private final HBox btnBar = new HBox();
    private final Button cancelBtn = new Button("Cancel");
    private final Button editBtn = new Button("Edit");
    private final Button saveBtn = new Button("Save");

    private final Label titleLbl1 = new Label("Title:");
    private final Label titleLbl2 = new Label();
    private final TextField titleTf = new TextField();
    private StackPane titleSp;

    private final Label createdDateLbl1 = new Label("Created Date:");
    private final Label createdDateLbl2 = new Label();

    private final Label noteTypeLbl1 = new Label("Note Type:");
    private final Label noteTypeLbl2 = new Label();

    private final ComboBox<String> typeCb = new ComboBox<>();
    private StackPane noteTypeSp;
    private final StackPane categoryPriorityLblSp = new StackPane();
    private final StackPane categoryPriorityCbSp = new StackPane();
    private StackPane deadlineSp;
    private final TextArea contentTa = new TextArea();

    private final Note note;
    private final NoteType type;

    private final NoteBinder noteBinder = new NoteBinder();
    private final InfoDialogActions actions = new InfoDialogActions();
    private final ExecutorService executor =
            ExecutorServiceManager.createCachedThreadPool(this.getClass().getSimpleName());

    private Pair<Runnable, Boolean> secondInitialization;
    private boolean secondElementInitialized = false;

    public NoteInfoDialog(Note note, TableView<Note> notesTbl) {

        scene = new Scene(gp, 400, 500);

        noteBinder.setNotesTbl(notesTbl);

        this.note = note;
        this.type = note.getNoteType();

        loadComponents();
    }

    private void loadComponents() {
        CompletableFuture<Boolean> uiFuture = CompletableFuture.supplyAsync(this::initializeUI, executor);
        CompletableFuture<Optional<String>> contentFuture =
                CompletableFuture.supplyAsync(() -> loadContentIfNeeded(note), executor);

        uiFuture.thenCombineAsync(contentFuture, (uiInitialized, contentOpt) -> {
                    if (!uiInitialized) {
                        return false;
                    }

                    contentOpt.ifPresent(note::setContent);
                    Platform.runLater(() -> noteBinder.withNote(note));
                    return true;
                }, executor)
                .exceptionally(ext -> null);
    }

    public void useDialog() {

        Platform.runLater(() -> {
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        });
    }

    private Boolean initializeUI() {
        try {
            Platform.runLater(() -> {
                initBaseElements();
                boundSizeProperties();
                loadStyles();
            });
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Optional<String> loadContentIfNeeded(Note note) {

        if (note.getContent() != null && !note.getContent().isBlank()) {
            return Optional.empty();
        }

        String content = noteBinder.getContent(note.getNoteId());
        return Optional.ofNullable(content);
    }

    private void initBaseElements() {

        List<Runnable> baseTasks = List.of(
                this::manageTitle,
                this::manageCreatedDate,
                this::manageType,
                () -> {
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);
                    saveBtn.setOnAction(evt -> {
                        if (noteBinder.save()) {
                            noteBinder.clearFields();
                            swapVisible();
                        }
                    });
                    saveBtn.setVisible(false);

                    cancelBtn.setOnAction(e -> stage.close());
                    btnBar.getChildren().addAll(cancelBtn, spacer, editBtn, saveBtn);
                    noteBinder.withContent(contentTa);
                    contentTa.setEditable(false);

                    gp.add(btnBar, 0, 0, 2, 1);
                    gp.add(contentTa, 0, 6, 2, 3);
                }
        );
        baseTasks.forEach(task -> executor.submit(() -> Platform.runLater(task)));
        loadButtonsActions();
    }

    private void loadButtonsActions() {
        editBtn.setOnAction(ect -> swapVisible());
    }

    private void manageTitle() {

        noteBinder.withTitleLbl(titleLbl2);

        noteBinder.withTitleTf(titleTf);
        titleTf.setVisible(false);

        titleSp = new StackPane(titleTf, titleLbl2);
        gp.add(titleLbl1, 0, 1);
        gp.add(titleSp, 1, 1);
    }

    private void manageCreatedDate() {

        noteBinder.withNoteCreatedDate(createdDateLbl2);

        gp.add(createdDateLbl1, 0, 2);
        gp.add(createdDateLbl2, 1, 2);
    }

    private void manageType() {

        noteBinder.withNoteTypeLbl(noteTypeLbl2);

        noteBinder.withNoteTypeCb(typeCb);
        typeCb.setVisible(false);
        noteTypeSp = new StackPane(typeCb, noteTypeLbl2);

        if (type.equals(NoteType.REGULAR_NOTE)) {
            loadRegularNoteFields();
        } else {
            loadDeadlineNoteFields();
        }

        CompletableFuture.runAsync(() -> actions.setTypeCb(typeCb), executor)
                .thenRunAsync(() -> actions.setCategoryPriorityLblSp(categoryPriorityLblSp), executor)
                .thenRunAsync(() -> actions.setCategoryPriorityCbSp(categoryPriorityCbSp), executor)
                .join();

        gp.add(noteTypeLbl1, 0, 3);
        gp.add(noteTypeSp, 1, 3);
        gp.add(categoryPriorityLblSp, 0, 4);
        gp.add(categoryPriorityCbSp, 1, 4);
    }

    private void loadRegularNoteFields() {

        secondInitialization = new Pair<>(this::loadDeadlineNoteFields, secondElementInitialized);

        Label categoryLbl1 = new Label("Category:");
        Label categoryLbl2 = new Label();
        noteBinder.withNoteCategoryLbl(categoryLbl2);
        CompletableFuture.runAsync(() -> actions.setCategoryLbl(categoryLbl1), executor);

        categoryLbl1.setVisible(!secondInitialization.getValue());
        categoryLbl2.setVisible(!secondInitialization.getValue());

        ComboBox<String> categoryCb = new ComboBox<>();
        noteBinder.withNoteCategoryCb(categoryCb);
        categoryCb.setVisible(false);
        CompletableFuture.runAsync(() -> actions.setCategoryCb(categoryCb), executor);

        StackPane categorySp = new StackPane(categoryCb, categoryLbl2);
        categorySp.setVisible(!secondElementInitialized);

        categoryPriorityLblSp.getChildren().add(categoryLbl1);
        categoryPriorityCbSp.getChildren().add(categorySp);

        loadRNStyles(categoryLbl1, categoryLbl2, categoryCb);
    }

    private void loadRNStyles(Label categoryLbl1, Label categoryLbl2, ComboBox<String> categoryCb) {
        Platform.runLater(() -> {
            List.of(categoryLbl1, categoryLbl2)
                    .forEach(e -> e.getStyleClass().add("noteInfoDialogLbl"));

            categoryCb.getStyleClass().add("notesDialogComboBox");
        });
    }

    private void loadDeadlineNoteFields() {

        secondInitialization = new Pair<>(this::loadRegularNoteFields, secondElementInitialized);

        Label priorityLbl1 = new Label("Priority:");
        Label priorityLbl2 = new Label();
        noteBinder.withNotePriorityLbl(priorityLbl2);
        priorityLbl1.setVisible(!secondInitialization.getValue());
        priorityLbl2.setVisible(!secondInitialization.getValue());
        CompletableFuture.runAsync(() -> actions.setPriorityLbl(priorityLbl1), executor);

        ComboBox<String> priorityCb = new ComboBox<>();
        noteBinder.withNotePriorityCb(priorityCb);
        priorityCb.setVisible(false);
        CompletableFuture.runAsync(() -> actions.setPriorityCb(priorityCb), executor);

        StackPane prioritySp = new StackPane(priorityCb, priorityLbl2);
        prioritySp.setVisible(!secondElementInitialized);

        categoryPriorityLblSp.getChildren().addAll(priorityLbl1);
        categoryPriorityCbSp.getChildren().addAll(prioritySp);

        Label deadlineLbl1 = new Label("Deadline:");
        Label deadlineLbl2 = new Label();
        noteBinder.withNoteDeadlineLbl(deadlineLbl2);
        deadlineLbl1.setVisible(!secondInitialization.getValue());
        deadlineLbl2.setVisible(!secondInitialization.getValue());
        CompletableFuture.runAsync(() -> actions.setDeadlineLbl(deadlineLbl1));

        DatePicker deadlineDp = noteBinder.withNoteDeadlineDp();
        deadlineDp.setDayCellFactory(p -> new DateCell());
        deadlineDp.setVisible(secondInitialization.getValue());
        deadlineSp = new StackPane(deadlineDp, deadlineLbl2);
        deadlineSp.setVisible(!secondInitialization.getValue());
        CompletableFuture.runAsync(() -> actions.setDatePicker(deadlineDp));
        CompletableFuture.runAsync(() -> actions.setDeadlineSp(deadlineSp));

        gp.add(deadlineLbl1, 0, 5);
        gp.add(deadlineSp, 1, 5);

        loadDNStyles(priorityLbl1, priorityLbl2, deadlineLbl1, deadlineLbl2, priorityCb);
    }

    private void loadDNStyles(Label priorityLbl1, Label priorityLbl2, Label deadlineLbl1,
                              Label deadlineLbl2, ComboBox<String> priorityCb) {

        List.of(priorityLbl1, priorityLbl2, deadlineLbl1, deadlineLbl2)
                .forEach(e -> e.getStyleClass().add("noteInfoDialogLbl"));

        priorityCb.getStyleClass().add("notesDialogComboBox");
    }

    private void swapVisible() {

        secondElementInitialized = !secondElementInitialized;
        contentTa.setEditable(secondElementInitialized);
        saveBtn.setVisible(secondElementInitialized);

        if (!secondInitialization.getValue()) {
            secondInitialization.getKey().run();
            actions.useListener();
        } else if (!secondElementInitialized) {
            typeCb.getSelectionModel().select(Note.getName(note.getNoteType()));
        }
        swap();
    }

    private void swap() {
        for (StackPane sp : List.of(titleSp, noteTypeSp, categoryPriorityCbSp, deadlineSp)) {
            sp.getChildren().forEach(e -> {
                if (e instanceof StackPane esp) {
                    if (esp.isVisible()) {
                        esp.getChildren().forEach(l -> l.setVisible(!l.isVisible()));
                    } else {
                        esp.getChildren().forEach(l -> l.setVisible(false));
                    }
                } else {
                    e.setVisible(!e.isVisible());
                }
            });
        }
    }

    private void boundSizeProperties() {
        Screen screen = Screen.getPrimary();
        double width = screen.getBounds().getWidth();
        double windowWidth = width * 0.20;

        stage.setMinWidth(windowWidth);
        stage.setMaxWidth(windowWidth);
    }

    private void loadStyles() {
        URL resource = getClass().getResource("/View/Styles/Dialogs/NoteInfoDialog.css");
        if (resource == null) {
            return;
        }
        scene.getStylesheets().add(resource.toExternalForm());

        Platform.runLater(() -> {

            titleTf.getStyleClass().add("noteInfoDialogTf");

            gp.setPadding(new Insets(10, 10, 10, 10));
            gp.getStyleClass().add("noteInfoDialogGp");

            contentTa.getStyleClass().add("noteInfoDialogTextArea");

            List.of(cancelBtn, saveBtn, editBtn)
                    .forEach(e -> e.getStyleClass().add("noteInfoDialogBtn"));
            List.of(titleLbl1, createdDateLbl1, noteTypeLbl1,
                            titleLbl2, noteTypeLbl2, createdDateLbl2)
                    .forEach(e -> e.getStyleClass().add("noteInfoDialogLbl"));


            typeCb.getStyleClass().add("notesDialogComboBox");
        });
    }
}