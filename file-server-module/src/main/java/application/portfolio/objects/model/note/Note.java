package application.portfolio.objects.model.note;

import application.portfolio.objects.dao.note.NoteDAO;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

public class Note {

    private UUID noteId;
    private String title;
    private LocalDateTime createdDate;
    private LocalDateTime lastModificationDate;
    private String content;
    private NoteType noteType;
    private Category category;
    private Priority priority;
    private LocalDate deadline;

    public Note() {
    }

    public UUID getNoteId() {
        return noteId;
    }

    public void setNoteId(UUID noteId) {
        this.noteId = noteId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getLastModificationDate() {
        return lastModificationDate;
    }

    public void setLastModificationDate(LocalDateTime lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public NoteType getNoteType() {
        return noteType;
    }

    public void setNoteType(NoteType noteType) {
        this.noteType = noteType;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public static Note readNoteMeta(BufferedReader reader) {

        Note note = new Note();
        try {
            String line;
            while ((line = reader.readLine()) != null && line.startsWith("#")) {

                String[] keyVal = line.substring(1).split(":", 2);
                if (keyVal.length < 2) continue;

                String key = keyVal[0].trim();
                String val = keyVal[1].trim();
                createNote(note, key, val);
            }
        } catch (IOException | IllegalArgumentException | DateTimeParseException e) {
            return null;
        }
        return note;
    }

    public static Note createNote(JsonNode node) {

        Note note = new Note();
        Iterator<Map.Entry<String, JsonNode>> iterator = node.fields();

        try {
            while (iterator.hasNext()) {
                Map.Entry<String, JsonNode> entry = iterator.next();
                String key = entry.getKey();
                String val = entry.getValue().asText();
                createNote(note, key, val);
            }
        } catch (IllegalArgumentException | DateTimeParseException e) {
            return null;
        }
        return note;
    }

    private static void createNote(Note note, String key, String val) {
        switch (key) {
            case "noteId" -> {
                UUID id = UUID.fromString(val);
                note.setNoteId(id);
            }
            case "title" -> note.setTitle(val);
            case "createdDate" -> {
                LocalDateTime ldt = LocalDateTime.parse(val).withNano(0);
                note.setCreatedDate(ldt);
            }
            case "lastModificationDate" -> {
                LocalDateTime ldt = LocalDateTime.parse(val).withNano(0);
                note.setLastModificationDate(ldt);
            }
            case "content" -> note.setContent(val);
            case "noteType" -> {
                int v = Integer.parseInt(val);
                NoteType nt = NoteType.fromValue(v);
                note.setNoteType(nt);
            }
            case "category" -> {
                int v = Integer.parseInt(val);
                if (v >= 0) {
                    Category ct = Category.fromValue(v);
                    note.setCategory(ct);
                }
            }
            case "priority" -> {
                int v = Integer.parseInt(val);
                if (v >= 0) {
                    Priority pt = Priority.fromValue(v);
                    note.setPriority(pt);
                }
            }
            case "deadline" -> {
                if (!val.isBlank() && !val.equals("null")) {
                    LocalDate dl = LocalDate.parse(val);
                    note.setDeadline(dl);
                }
            }
        }
    }

    public static NoteDAO createDAO(Note note) {

        NoteDAO noteDAO = new NoteDAO();
        noteDAO.setNoteId(note.getNoteId().toString());
        noteDAO.setTitle(note.getTitle());
        noteDAO.setCreatedDate(note.getCreatedDate().toString());
        noteDAO.setLastModificationDate(note.getLastModificationDate().toString());
        noteDAO.setContent(note.getContent());

        int noteType = note.getNoteType().getValue();
        noteDAO.setNoteType(note.getNoteType().getValue());
        switch (noteType) {
            case 0 -> noteDAO.setCategory(note.getCategory().getValue());
            case 1 -> {
                noteDAO.setPriority(note.getPriority().getValue());
                noteDAO.setDeadline(note.getDeadline().toString());
            }
        }
        return noteDAO;
    }

    public static List<NoteDAO> convertToDAOCollection(Collection<Note> collection) {
        return collection.stream()
                .map(Note::createDAO)
                .toList();
    }
}
