package application.portfolio.clientmodule.Model.Request.Notes;

import application.portfolio.clientmodule.Model.Model.Notes.Category;
import application.portfolio.clientmodule.Model.Model.Notes.NoteType;
import application.portfolio.clientmodule.Model.Model.Notes.Priority;
import application.portfolio.clientmodule.Model.Request.Notes.NoteRequest.BaseNoteRequest;
import application.portfolio.clientmodule.Model.Request.Notes.NoteRequest.DeadlineNoteRequest;
import application.portfolio.clientmodule.Model.Request.Notes.NoteRequest.NoteRequest;
import application.portfolio.clientmodule.Model.Request.Notes.NoteRequest.RegularNoteRequest;
import application.portfolio.clientmodule.Model.Model.Notes.NoteDAO;
import application.portfolio.clientmodule.utils.DateUtils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class NotesRequestViewModel {

    private NoteDAO noteDAO = null;

    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty noteType = new SimpleStringProperty();
    private final StringProperty category = new SimpleStringProperty();
    private final StringProperty priority = new SimpleStringProperty();
    private final StringProperty content = new SimpleStringProperty();
    private final StringProperty createdDate = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> deadline = new SimpleObjectProperty<>();

    private final NotesRequestConverter converter = new NotesRequestConverter();
    private final NoteRequestModel model = new NoteRequestModel();

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getNoteType() {
        return noteType.get();
    }

    public StringProperty noteTypeProperty() {
        return noteType;
    }

    public void setNoteType(String noteType) {
        this.noteType.set(noteType);
    }

    public String getCategory() {
        return category.get();
    }

    public StringProperty categoryProperty() {
        return category;
    }

    public void setCategory(String category) {
        this.category.set(category);
    }

    public String getPriority() {
        return priority.get();
    }

    public StringProperty priorityProperty() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority.set(priority);
    }

    public String getContent() {
        return content.get();
    }

    public StringProperty contentProperty() {
        return content;
    }

    public void setContent(String content) {
        this.content.set(content);
    }

    public String getCreatedDate() {
        return createdDate.get();
    }

    public StringProperty createdDateProperty() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate.set(createdDate);
    }

    public LocalDate getDeadline() {
        return deadline.get();
    }

    public ObjectProperty<LocalDate> deadlineProperty() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline.set(deadline);
    }

    public void setNoteDAO(NoteDAO noteDAO) {

        this.noteDAO = noteDAO;
        unpackData();
    }

    private void unpackData() {

        if (noteDAO == null) {
            return;
        }

        setTitle(noteDAO.getTitle());

        String formatedDate = DateUtils.formatCreatedDate(noteDAO.getCreatedDate());
        setCreatedDate(formatedDate);
        setContent(noteDAO.getContent());

        if (noteDAO.getNoteType().equals(NoteType.DEADLINE_NOTE)) {
            setDeadline(noteDAO.getDeadline());
        }
    }

    private void packData(NoteDAO note) {

        NoteType type = note.getNoteType();
        note.setCreatedDate(LocalDateTime.now());

        if (type.equals(NoteType.REGULAR_NOTE)) {
            note.setCategory(NoteDAO.stringToEnum(Category.class, getCategory()));
        } else if (type.equals(NoteType.DEADLINE_NOTE)) {
            note.setPriority(NoteDAO.stringToEnum(Priority.class, getPriority()));
            note.setDeadline(getDeadline());
        }

    }

    /*public void save() {

        NoteDAO note = new NoteDAO(
                getTitle(),
                NoteDAO.stringToEnum(NoteType.class, getNoteType()),
                getContent());

        packData(note);
        sendRequest(note);
    }*/

    public void update() {

        if (!getTitle().equals(noteDAO.getTitle())) {
            noteDAO.setTitle(getTitle());
        }

        if (!getContent().equals(noteDAO.getContent())) {
            noteDAO.setContent(getContent());
        }

        if (getNoteType().equals(NoteDAO.getName(noteDAO.getNoteType()))) {
            if (noteDAO.getNoteType().equals(NoteType.REGULAR_NOTE)) {
                Category category = NoteDAO.stringToEnum(Category.class, getCategory());
                if (!category.equals(noteDAO.getCategory())) {
                    noteDAO.setCategory(category);
                }
            } else {
                Priority priority = NoteDAO.stringToEnum(Priority.class, getPriority());
                if (!priority.equals(noteDAO.getPriority())) {
                    noteDAO.setPriority(priority);
                }

                if (!getDeadline().equals(noteDAO.getDeadline())) {
                    System.out.println("Changed date");
                    noteDAO.setDeadline(getDeadline());
                }
            }
        } else {
            NoteType type = NoteDAO.stringToEnum(NoteType.class, getNoteType());
            if (type.equals(NoteType.REGULAR_NOTE)) {
                Category category = NoteDAO.stringToEnum(Category.class, getCategory());
                noteDAO.setCategory(category);
            } else {
                Priority priority = NoteDAO.stringToEnum(Priority.class, getPriority());
                noteDAO.setPriority(priority);

                System.out.println("Changed date");
                noteDAO.setDeadline(getDeadline());
            }
            noteDAO.setNoteType(type);
        }
        sendRequest(noteDAO);
    }

    private void sendRequest(NoteDAO note) {

        CompletableFuture.runAsync(() -> {
            BaseNoteRequest<?> data = converter.toNoteRequest(note);

            if (data instanceof RegularNoteRequest d) {
                model.save(d);
            } else if (data instanceof DeadlineNoteRequest d) {
                model.save(d);
            } else {
                System.out.println("Problem");
            }
        });

        CompletableFuture.runAsync(this::clearing);
    }

    private void clearing() {
        /*CompletableFuture.runAsync(NoteBinder::clearStartFields, executor);
        CompletableFuture.runAsync(NoteBinder::clearChangingBinds, executor);*/
    }

    public List<NoteDAO> loadNotes() {
        NoteRequest data = converter.convertToLoadRequest();
        return model.loadNotes(data);
    }

    public String loadNoteContent(UUID noteId) {
        NoteRequest data = converter.convertToNoteContentRequest(noteId);
        return model.loadContent(data);
    }
}
