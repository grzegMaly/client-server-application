package application.portfolio.clientmodule.Model.Model.Notes;

import application.portfolio.clientmodule.utils.DataParser;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

public class NoteDAO {

    private static final String[] REQUIRED_ATTRIBUTES = {"noteId", "title", "createdDate", "lastModificationDate",
            "noteType", "category", "priority"};

    private UUID id;
    private String title;
    private NoteType noteType;
    private Category category;
    private Priority priority;
    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime lastModificationDate;
    private LocalDate deadline;

    public NoteDAO() {
        setCreatedDate(LocalDateTime.now());
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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
                .map(NoteDAO::enumToString)
                .collect(Collectors.toList());
    }

    public static <T extends Enum<T>> String getName(T val) {
        return val != null ? enumToString(val) : null;
    }

    public static NoteDAO createDAO(JsonNode node) {

        if (!DataParser.validateElements(node, REQUIRED_ATTRIBUTES)) {
            return null;
        }

        NoteDAO dao = new NoteDAO();
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
                        dao.setId(nId);
                    }
                    case "title" -> {
                        String title = value.asText();
                        dao.setTitle(title);
                    }
                    case "createdDate" -> {
                        String createdDateString = value.asText();
                        LocalDateTime cD = LocalDateTime.parse(createdDateString);
                        dao.setCreatedDate(cD);
                    }
                    case "lastModificationDate" -> {
                        String lastModificationDate = value.asText();
                        LocalDateTime lMD = LocalDateTime.parse(lastModificationDate);
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
}