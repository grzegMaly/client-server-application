package application.portfolio.clientmodule.Model.Model.Notes;

import application.portfolio.clientmodule.utils.DataParser;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

public class Note {

    private static final String[] REQUIRED_ATTRIBUTES = {"noteId", "title", "createdDate", "lastModificationDate",
            "noteType", "category", "priority"};

    private UUID noteId = UUID.randomUUID();
    private String title;
    private NoteType noteType;
    private Category category;
    private Priority priority;
    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime lastModificationDate;
    private LocalDate deadline;

    public Note() {
        setCreatedDate(LocalDateTime.now().withNano(0));
        setLastModificationDate(LocalDateTime.now().withNano(0));
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public static <T extends Enum<T>> T stringToEnum(Class<T> classClass, String val) {
        return Enum.valueOf(classClass, val.replaceAll(" ", "_").toUpperCase());
    }

    public static <T extends Enum<T>> String enumToString(T val) {
        return Arrays.stream(val.name().split("_"))
                .map(s -> s.charAt(0) + s.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }

    public static <T extends Enum<T>> List<String> getNames(Class<T> classClass) {
        return EnumSet.allOf(classClass)
                .stream()
                .map(Note::enumToString)
                .collect(Collectors.toList());
    }

    public static <T extends Enum<T>> String getName(T val) {
        return val != null ? enumToString(val) : null;
    }

    public static Note createNote(JsonNode node) {

        if (!DataParser.validateElements(node, REQUIRED_ATTRIBUTES)) {
            return null;
        }

        Note dao = new Note();
        Iterator<Map.Entry<String, JsonNode>> it = node.fields();
        try {
            while (it.hasNext()) {
                Map.Entry<String, JsonNode> entry = it.next();
                String key = entry.getKey();
                JsonNode value = entry.getValue();
                switch (key) {
                    case "noteId" -> {
                        String id = value.asText();
                        UUID nId = DataParser.parseId(id);
                        if (nId == null) break;
                        dao.setNoteId(nId);
                    }
                    case "title" -> {
                        String title = value.asText();
                        dao.setTitle(title);
                    }
                    case "createdDate" -> {
                        String createdDateString = value.asText();
                        LocalDateTime cD = LocalDateTime.parse(createdDateString).withNano(0);
                        dao.setCreatedDate(cD);
                    }
                    case "lastModificationDate" -> {
                        String lastModificationDate = value.asText();
                        LocalDateTime lMD = LocalDateTime.parse(lastModificationDate).withNano(0);
                        dao.setLastModificationDate(lMD);
                    }
                    case "noteType" -> {
                        int noteType = value.asInt();
                        NoteType nt = NoteType.fromValue(noteType);
                        dao.setNoteType(nt);
                    }
                    case "category" -> {
                        int category = value.asInt();
                        if (category >= 0) {
                            Category ct = Category.fromValue(category);
                            dao.setCategory(ct);
                        }
                    }
                    case "priority" -> {
                        int priority = value.asInt();
                        if (priority >= 0) {
                            Priority pt = Priority.fromValue(priority);
                            dao.setPriority(pt);
                        }
                    }
                    case "deadline" -> {
                        if (!value.isNull()) {
                            String deadline = value.asText();
                            if (deadline != null) {
                                LocalDate dl = LocalDate.parse(deadline);
                                dao.setDeadline(dl);
                            }
                        }
                    }
                }
            }
        } catch (IllegalArgumentException | DateTimeParseException e) {
            return null;
        }
        return dao;
    }

    public static NoteDAO createDAO(Note note) {

        try {
            return new NoteDAO(
                    note.getNoteId().toString(),
                    note.getTitle(),
                    note.getCreatedDate().toString(),
                    note.getLastModificationDate().toString(),
                    note.getContent(),
                    note.getNoteType().getValue(),
                    Optional.ofNullable(note.getCategory()).map(Category::getValue).orElse(-1),
                    Optional.ofNullable(note.getPriority()).map(Priority::getValue).orElse(-1),
                    note.getDeadline() == null ? null : note.getDeadline().toString()
            );
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Note note = (Note) o;
        return getNoteId().equals(note.getNoteId()) &&
                getTitle().equals(note.getTitle()) &&
                getNoteType() == note.getNoteType() &&
                getCategory() == note.getCategory() &&
                getPriority() == note.getPriority() &&
                Objects.equals(getContent(), note.getContent()) &&
                getCreatedDate().equals(note.getCreatedDate()) &&
                Objects.equals(getDeadline(), note.getDeadline());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                getNoteId(),
                getTitle(),
                getNoteType(),
                getCategory(),
                getPriority(),
                getContent(),
                getCreatedDate(),
                getDeadline()
        );
    }

    @Override
    public String toString() {
        return "Note{" +
                "noteId=" + noteId +
                ", title='" + title + '\'' +
                ", noteType=" + noteType +
                ", category=" + category +
                ", priority=" + priority +
                ", content='" + content + '\'' +
                ", createdDate=" + createdDate +
                ", lastModificationDate=" + lastModificationDate +
                ", deadline=" + deadline +
                '}';
    }
}