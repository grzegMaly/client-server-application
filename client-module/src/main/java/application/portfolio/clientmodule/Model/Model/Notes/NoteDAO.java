package application.portfolio.clientmodule.Model.Model.Notes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class NoteDAO {

    public enum NoteType {
        REGULAR_NOTE, DEADLINE_NOTE
    }

    public enum Category {
        SHOPPING, MEETING, DIARY, RECEIPT;
    }

    public enum Priority {
        HIGH, MEDIUM, LOW
    }

    private UUID id;
    private String title;
    private NoteType noteType;
    private Category category;
    private Priority priority;
    private String content;
    private LocalDateTime createdDate;
    private LocalDate deadline;

    public NoteDAO(String title, NoteType noteType, String content) {
        this.title = title;
        this.noteType = noteType;
        this.content = content;
        setCreatedDate(LocalDateTime.now());
    }

    public UUID getId() {
        return id;
    }

    public void setId(String id) {
        this.id = UUID.fromString(id);
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

    public static  <T extends Enum<T>> String getName(T val) {
        return val != null ? enumToString(val) : null;
    }
}