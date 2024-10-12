package application.portfolio.clientmodule.Model.View.LeftBarCards.Notes.NoteUtils;

import application.portfolio.clientmodule.Model.Request.Notes.NotesRequestViewModel;
import application.portfolio.clientmodule.OtherElements.NoteDAO;
import application.portfolio.clientmodule.utils.DateUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class NoteBinder {

    private NoteDAO note = null;
    private final NotesRequestViewModel viewModel = new NotesRequestViewModel();

    private TextField titleTf;
    private ComboBox<String> typeCb;
    private ComboBox<String> categoryCb;
    private ComboBox<String> priorityCb;
    private DatePicker datePicker;
    private TextArea contentTa;

    private static final Set<Runnable> startBinds = new HashSet<>();
    private static final Set<Runnable> changingBinds = new HashSet<>();

    public void withNoteDAO(NoteDAO noteDAO) {

        note = noteDAO;
        viewModel.setNoteDAO(noteDAO);

        changingBinds.add(() -> {
            note = null;
            viewModel.setNoteDAO(null);
        });
    }

    public void withTitleLbl(Label titleLbl) {

        Runnable action = () -> titleLbl.textProperty().bindBidirectional(viewModel.titleProperty());
        action.run();

        changingBinds.add(() -> {
                    viewModel.setTitle(null);
                    titleLbl.textProperty().unbindBidirectional(viewModel.titleProperty());
                }
        );
    }

    public void withTitleTf(TextField titleTf) {

        this.titleTf = titleTf;

        Runnable action = () -> this.titleTf.textProperty().bindBidirectional(viewModel.titleProperty());
        startBinds.add(() -> viewModel.setTitle(null));

        action.run();
    }

    public void withNoteCreatedDate(Label createdDateLbl) {

        Runnable action = () -> createdDateLbl.textProperty().bindBidirectional(viewModel.createdDateProperty());
        action.run();

        changingBinds.add(() -> {
            viewModel.setCreatedDate(null);
            createdDateLbl.textProperty().unbindBidirectional(viewModel.createdDateProperty());
        });
    }

    public void withNoteTypeLbl(Label noteTypeLbl) {

        Runnable action = () -> noteTypeLbl.textProperty().bind(viewModel.noteTypeProperty());
        action.run();

        changingBinds.add(() -> {
                    noteTypeLbl.textProperty().unbind();
                    noteTypeLbl.setText(null);
                }
        );
    }

    public void withNoteTypeCb(ComboBox<String> typeCb) {

        this.typeCb = typeCb;
        Runnable action = () -> viewModel.noteTypeProperty().bind(this.typeCb.valueProperty());

        ObservableList<String> elements = FXCollections.observableList(NoteDAO.getNames(NoteDAO.NoteType.class));
        this.typeCb.setItems(elements);

        if (note != null) {
            String type = NoteDAO.getName(note.getNoteType());
            if (type != null) this.typeCb.getSelectionModel().select(type);
        } else {
            this.typeCb.getSelectionModel().select(0);
        }
        action.run();
    }

    public void withNoteCategoryLbl(Label noteCategoryLbl) {

        Runnable action = () -> noteCategoryLbl.textProperty().bind(viewModel.categoryProperty());
        action.run();

        changingBinds.add(() -> {
                    noteCategoryLbl.textProperty().unbind();
                    noteCategoryLbl.setText(null);
                }
        );
    }

    public void withNoteCategoryCb(ComboBox<String> categoryCb) {

        this.categoryCb = categoryCb;
        Runnable action = () -> viewModel.categoryProperty().bind(this.categoryCb.valueProperty());
        String CATEGORY = "Category";

        ObservableList<String> elements = FXCollections.observableArrayList(NoteDAO.getNames(NoteDAO.Category.class));
        elements.addFirst(CATEGORY);
        this.categoryCb.setItems(elements);

        if (note != null) {
            String category = NoteDAO.getName(note.getCategory());
            if (category != null) this.categoryCb.getSelectionModel().select(category);
        } else {
            this.categoryCb.getSelectionModel().select(0);
        }
        action.run();
    }

    public void withNotePriorityLbl(Label priorityLbl) {

        Runnable action = () -> priorityLbl.textProperty().bind(viewModel.priorityProperty());
        action.run();

        changingBinds.add(() -> {
                    priorityLbl.textProperty().unbind();
                    priorityLbl.setText(null);
                }
        );
    }

    public void withNotePriorityCb(ComboBox<String> priorityCb) {

        this.priorityCb = priorityCb;
        Runnable action = () -> viewModel.priorityProperty().bind(this.priorityCb.valueProperty());

        String PRIORITY = "Priority";

        ObservableList<String> elements =
                FXCollections.observableList(NoteDAO.getNames(NoteDAO.Priority.class));
        elements.addFirst(PRIORITY);
        this.priorityCb.setItems(elements);

        if (note != null) {
            String priority = NoteDAO.getName(note.getPriority());
            if (priority != null) priorityCb.getSelectionModel().select(priority);
        } else {
            priorityCb.getSelectionModel().select(0);
        }
        action.run();
    }

    public void withNoteDeadlineLbl(Label deadlineLbl) {

        Runnable action = () -> {
            String date = DateUtils.formatDeadlineDate(viewModel.getDeadline());
            deadlineLbl.setText(date);
        };
        action.run();
        changingBinds.add(() -> deadlineLbl.setText(null));
    }

    public void withNoteDeadlineDp(DatePicker datePicker) {

        this.datePicker = datePicker;
        Runnable action = () -> {
            if (note != null) {
                this.datePicker.setValue(note.getDeadline());
            } else {
                this.datePicker.setValue(LocalDate.now());
            }
            this.datePicker.valueProperty().bindBidirectional(viewModel.deadlineProperty());
        };
        this.datePicker.setEditable(false);
        startBinds.add(() -> viewModel.setDeadline(null));
        action.run();
    }

    public void withContent(TextArea contentTa) {

        this.contentTa = contentTa;
        Runnable action = () -> this.contentTa.textProperty().bindBidirectional(viewModel.contentProperty());
        this.contentTa.setWrapText(true);
        startBinds.add(() -> viewModel.setContent(null));
        action.run();
    }

    public void clearStartFields() {
        Platform.runLater(() -> startBinds.forEach(Runnable::run));
    }

    public void clearChangingBinds() {
        Platform.runLater(() -> changingBinds.forEach(Runnable::run));
        changingBinds.clear();
    }

    public void save() {

        if (!validateElements()) {
//            addListeners();
            return;
        }

        if (note == null) {
            viewModel.save();
        } else {
            viewModel.update();
        }
    }

    private boolean validateElements() {

        boolean flag = true;

        if (titleTf.getText().isEmpty() || titleTf.getText().isBlank()) {
//            titleTf.getStyleClass().add("badElement");
            flag = false;
        }

        if (contentTa.getText().isEmpty() || contentTa.getText().isBlank()) {
//            contentTa.getStyleClass().add("badElement");
            flag = false;
        }

        if (typeCb.getValue().equals(NoteDAO.getName(NoteDAO.NoteType.REGULAR_NOTE))) {
            if (categoryCb.getValue().equals("Category")) {
//                cbCategory.getStyleClass().add("badElement");
                flag = false;
            }
        } else if (typeCb.getValue().equals(NoteDAO.getName(NoteDAO.NoteType.DEADLINE_NOTE))) {
            if (priorityCb.getValue().equals("Priority")) {
//                cbPriority.getStyleClass().add("badElement");
                flag = false;
            }
        }
        return flag;
    }

    private void addListeners() {

        titleTf.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty() && !newValue.isBlank()) {
//                titleTf.getStyleClass().remove("badElement");
            }
        });

        contentTa.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty() && !newValue.isBlank()) {
//                contentTa.getStyleClass().remove("badElement");
            }
        });

        typeCb.valueProperty().addListener((observable, oldValue, newValue) -> {
//            cbType.getStyleClass().remove("badElement");
        });

        categoryCb.valueProperty().addListener((observable, oldValue, newValue) -> {
//            cbCategory.getStyleClass().remove("badElement");
        });

        priorityCb.valueProperty().addListener((observable, oldValue, newValue) -> {
//            cbPriority.getStyleClass().remove("badElement");
        });
    }
}
