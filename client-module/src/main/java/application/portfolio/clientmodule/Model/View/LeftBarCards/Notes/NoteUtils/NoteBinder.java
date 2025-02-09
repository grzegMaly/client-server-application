package application.portfolio.clientmodule.Model.View.LeftBarCards.Notes.NoteUtils;

import application.portfolio.clientmodule.Model.Model.Notes.*;
import application.portfolio.clientmodule.Model.Request.Notes.NotesRequestViewModel;
import application.portfolio.clientmodule.Model.View.LeftBarCards.Notes.NoteController;
import application.portfolio.clientmodule.utils.DateUtils;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class NoteBinder {

    private Note bindedNote = null;
    private final NotesRequestViewModel viewModel = new NotesRequestViewModel();

    private TextField titleTf;
    private ComboBox<String> typeCb;
    private ComboBox<String> categoryCb;
    private ComboBox<String> priorityCb;
    private Label deadlineLbl;
    private TextArea contentTa;
    private NoteController noteController;
    private TableView<Note> notesTbl;

    private static final Set<Runnable> startBinds = new HashSet<>();
    private static final Set<Runnable> unbindingSet = new HashSet<>();

    public void bindNoteController(NoteController noteController) {
        this.noteController = noteController;
        noteController.setNoteBinder(this);
    }

    public void withNote(Note note) {

        this.bindedNote = note;
        viewModel.setTempNote(note);

        startBinds.add(() -> {
            this.bindedNote = null;
            viewModel.setTempNote(null);
        });
    }

    public void withTitleLbl(Label titleLbl) {

        titleLbl.textProperty().bindBidirectional(viewModel.titleProperty());
        unbindingSet.add(() -> {
                    viewModel.setTitle(null);
                    titleLbl.textProperty().unbindBidirectional(viewModel.titleProperty());
                }
        );
    }

    public void withTitleTf(TextField titleTf) {

        this.titleTf = titleTf;

        this.titleTf.textProperty().bindBidirectional(viewModel.titleProperty());

        startBinds.add(() -> {
            viewModel.setTitle("");
        });
        unbindingSet.add(() -> this.titleTf.textProperty().unbindBidirectional(viewModel.titleProperty()));
    }

    public void withNoteCreatedDate(Label createdDateLbl) {

        createdDateLbl.textProperty().bindBidirectional(viewModel.createdDateProperty());

        unbindingSet.add(() -> {
            viewModel.setCreatedDate(null);
            createdDateLbl.textProperty().unbindBidirectional(viewModel.createdDateProperty());
        });
    }

    public void withNoteTypeLbl(Label noteTypeLbl) {

        noteTypeLbl.textProperty().bind(viewModel.noteTypeProperty());

        unbindingSet.add(() -> {
                    noteTypeLbl.textProperty().unbind();
                    noteTypeLbl.setText(null);
                }
        );
    }

    public void withNoteTypeCb(ComboBox<String> typeCb) {

        this.typeCb = typeCb;
        viewModel.noteTypeProperty().bind(this.typeCb.valueProperty());

        ObservableList<String> elements = FXCollections.observableList(Note.getNames(NoteType.class));
        this.typeCb.setItems(elements);

        if (bindedNote != null) {
            String type = Note.getName(bindedNote.getNoteType());
            if (type != null) this.typeCb.getSelectionModel().select(type);
        } else {
            this.typeCb.getSelectionModel().select(0);
        }
    }

    public void withNoteCategoryLbl(Label noteCategoryLbl) {

        noteCategoryLbl.textProperty().bind(viewModel.categoryProperty());

        unbindingSet.add(() -> {
                    noteCategoryLbl.textProperty().unbind();
                    noteCategoryLbl.setText(null);
                }
        );
    }

    public void withNoteCategoryCb(ComboBox<String> categoryCb) {

        this.categoryCb = categoryCb;
        viewModel.categoryProperty().bind(this.categoryCb.valueProperty());

        String CATEGORY = "Category";
        ObservableList<String> elements = FXCollections.observableArrayList(Note.getNames(Category.class));
        elements.add(0, CATEGORY);
        this.categoryCb.setItems(elements);

        if (bindedNote != null) {
            String category = Note.getName(bindedNote.getCategory());
            if (category != null) {
                this.categoryCb.getSelectionModel().select(category);
            } else {
                this.categoryCb.getSelectionModel().select(0);
            }
        } else {
            this.categoryCb.getSelectionModel().select(0);
        }
    }

    public void withNotePriorityLbl(Label priorityLbl) {

        priorityLbl.textProperty().bind(viewModel.priorityProperty());
        unbindingSet.add(() -> {
                    priorityLbl.textProperty().unbind();
                    priorityLbl.setText(null);
                }
        );
    }

    public void withNotePriorityCb(ComboBox<String> priorityCb) {

        this.priorityCb = priorityCb;
        viewModel.priorityProperty().bind(this.priorityCb.valueProperty());

        String PRIORITY = "Priority";
        ObservableList<String> elements =
                FXCollections.observableList(Note.getNames(Priority.class));
        elements.add(0, PRIORITY);
        this.priorityCb.setItems(elements);

        if (bindedNote != null) {
            String priority = Note.getName(bindedNote.getPriority());
            if (priority != null) {
                priorityCb.getSelectionModel().select(priority);
            } else {
                priorityCb.getSelectionModel().select(0);
            }
        } else {
            priorityCb.getSelectionModel().select(0);
        }
    }

    public void withNoteDeadlineLbl(Label deadlineLbl) {

        this.deadlineLbl = deadlineLbl;
        this.deadlineLbl.textProperty().bind(
                Bindings.createStringBinding(
                        () -> {
                            LocalDate deadline = viewModel.getDeadline();
                            return deadline != null ? deadline.format(DateUtils.DEADLINE_FORMATTER) : "";
                        },
                        viewModel.deadlineProperty()
                ));

        unbindingSet.add(() -> {
            this.deadlineLbl.textProperty().unbind();
            this.deadlineLbl.setText(null);
        });
    }

    public DatePicker withNoteDeadlineDp() {

        DatePicker datePicker = new DatePicker();
        if (bindedNote != null) {
            datePicker.setValue(bindedNote.getDeadline());
        } else {
            datePicker.setValue(LocalDate.now());
        }
        datePicker.valueProperty().bindBidirectional(viewModel.deadlineProperty());

        datePicker.setEditable(false);

        startBinds.add(() -> viewModel.setDeadline(LocalDate.now()));
        unbindingSet.add(() -> {
            viewModel.setDeadline(null);
            datePicker.valueProperty().unbindBidirectional(viewModel.deadlineProperty());
        });
        return datePicker;
    }

    public void withContent(TextArea contentTa) {

        this.contentTa = contentTa;
        this.contentTa.textProperty().bindBidirectional(viewModel.contentProperty());

        this.contentTa.setWrapText(true);

        startBinds.add(() -> viewModel.setContent(""));
        unbindingSet.add(() -> this.contentTa.textProperty().unbindBidirectional(viewModel.contentProperty()));
    }

    public void clearFields() {

        if (bindedNote != null) {
            Platform.runLater(() -> {
                startBinds.forEach(Runnable::run);
                unbindingSet.forEach(Runnable::run);
            });
            startBinds.clear();
            unbindingSet.clear();
        } else {
            Platform.runLater(() -> startBinds.forEach(Runnable::run));
        }
    }

    public boolean save() {

        if (!validateElements()) {
            addListeners();
            return false;
        }

        if (bindedNote == null) {
            Note note = viewModel.save();
            if (note != null) {
                notesTbl.getItems().add(note);
                return true;
            }
        } else {
            Note note = viewModel.update();
            if (note != null) {
                notesTbl.getItems().remove(bindedNote);
                notesTbl.getItems().add(note);
                return true;
            }
        }
        return false;
    }

    public List<Note> loadNotes() {
        return viewModel.loadNotes();
    }

    public TableView<Note> loadNotesView() {
        setNotesTbl(noteController.getTableView());
        return this.notesTbl;
    }

    public String getContent(UUID noteId) {
        return viewModel.loadNoteContent(noteId);
    }

    public boolean delete(UUID noteId) {
        return viewModel.deleteNote(noteId);
    }

    private boolean validateElements() {

        boolean flag = true;

        if (titleTf.getText() == null || titleTf.getText().isBlank()) {
            titleTf.getStyleClass().add("badElement");
            flag = false;
        }

        if (contentTa.getText() == null || contentTa.getText().isBlank()) {
            contentTa.getStyleClass().add("badElement");
            flag = false;
        }

        if (typeCb.getValue().equals(Note.getName(NoteType.REGULAR_NOTE))) {
            if (categoryCb.getValue().equals("Category")) {
                categoryCb.getStyleClass().add("badElement");
                flag = false;
            }
        } else if (typeCb.getValue().equals(Note.getName(NoteType.DEADLINE_NOTE))) {
            if (priorityCb.getValue().equals("Priority")) {
                priorityCb.getStyleClass().add("badElement");
                flag = false;
            }
        }
        return flag;
    }

    private void addListeners() {

        titleTf.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isBlank()) {
                titleTf.getStyleClass().remove("badElement");
            }
        });

        contentTa.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isBlank()) {
                contentTa.getStyleClass().remove("badElement");
            }
        });

        typeCb.valueProperty().addListener((observable, oldValue, newValue) -> {
            typeCb.getStyleClass().remove("badElement");
        });

        categoryCb.valueProperty().addListener((observable, oldValue, newValue) -> {
            categoryCb.getStyleClass().remove("badElement");
        });

        priorityCb.valueProperty().addListener((observable, oldValue, newValue) -> {
            priorityCb.getStyleClass().remove("badElement");
        });
    }

    public void setNotesTbl(TableView<Note> notesTbl) {
        this.notesTbl = notesTbl;
    }

    public Button bindReloadBtn() {
        Button reloadBtn = new Button("Reload");
        reloadBtn.setOnAction(evt -> {
            List<Note> DAOs = loadNotes();
            notesTbl.getItems().clear();
            notesTbl.getItems().addAll(DAOs);
        });
        return reloadBtn;
    }

    public Button bindSaveBtn() {
        Button saveBtn = new Button("Save");
        saveBtn.setOnAction(evt -> {
            if (save()) clearFields();
        });
        return saveBtn;
    }
}
