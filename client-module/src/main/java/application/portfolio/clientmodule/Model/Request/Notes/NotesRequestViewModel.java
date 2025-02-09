package application.portfolio.clientmodule.Model.Request.Notes;

import application.portfolio.clientmodule.Model.Model.Notes.*;
import application.portfolio.clientmodule.Model.Request.Notes.NoteRequest.NoteRequest;
import application.portfolio.clientmodule.utils.DateUtils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class NotesRequestViewModel {

    private Note tempNote = null;

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

    public void setTempNote(Note tempNote) {

        this.tempNote = tempNote;
        unpackData();
    }

    public List<Note> loadNotes() {
        NoteRequest data = converter.convertToWithUserIdRequest();
        return model.loadNotes(data);
    }

    public String loadNoteContent(UUID noteId) {
        NoteRequest data = converter.convertToWithUIdAndNoteIdRequest(noteId);
        return model.loadContent(data);
    }

    public Note save() {
        Note note = packData();
        NoteDAO dao = Note.createDAO(note);
        if (dao == null) {
            return null;
        }

        NoteRequest data = converter.convertToWithUserIdRequest();
        boolean result = model.save(data, dao);
        if (result) {
            return note;
        }
        return null;
    }

    public Note update() {

        Note note = packData();
        note.setNoteId(tempNote.getNoteId());
        note.setCreatedDate(tempNote.getCreatedDate());
        note.setLastModificationDate(LocalDateTime.now().withNano(0));

        if (note.equals(tempNote)) {
            return null;
        }

        NoteDAO dao = Note.createDAO(note);
        if (dao == null) {
            return null;
        }

        NoteRequest data = converter.convertToWithUIdAndNoteIdRequest(note.getNoteId());
        boolean result = model.update(data, dao);
        if (result) {
            return note;
        }
        return null;
    }

    public boolean deleteNote(UUID noteId) {
        NoteRequest data = converter.convertToWithUIdAndNoteIdRequest(noteId);
        return model.delete(data);
    }

    private void unpackData() {

        if (tempNote == null) {
            return;
        }

        setTitle(tempNote.getTitle());

        String formatedDate = DateUtils.formatCreatedDate(tempNote.getCreatedDate());
        setCreatedDate(formatedDate);
        setContent(tempNote.getContent());

        if (tempNote.getNoteType().equals(NoteType.DEADLINE_NOTE)) {
            setDeadline(tempNote.getDeadline());
        }
    }

    private Note packData() {

        Note note = new Note();
        note.setTitle(getTitle());
        note.setContent(getContent());
        NoteType type = Note.stringToEnum(NoteType.class, getNoteType());
        note.setNoteType(type);
        note.setCreatedDate(LocalDateTime.now().withNano(0));
        note.setLastModificationDate(LocalDateTime.now().withNano(0));

        if (type.equals(NoteType.REGULAR_NOTE)) {
            note.setCategory(Note.stringToEnum(Category.class, getCategory()));
        } else if (type.equals(NoteType.DEADLINE_NOTE)) {
            note.setPriority(Note.stringToEnum(Priority.class, getPriority()));
            note.setDeadline(getDeadline());
        }
        return note;
    }
}
