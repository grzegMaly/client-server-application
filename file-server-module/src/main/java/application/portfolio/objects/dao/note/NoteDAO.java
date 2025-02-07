package application.portfolio.objects.dao.note;


public class NoteDAO {

    private String noteId;
    private String title;
    private String createdDate;
    private String lastModificationDate;
    private String content;
    private int noteType;
    private int category = -1;
    private int priority = -1;
    private String deadline;

    public NoteDAO() {
    }

    public NoteDAO(String noteId, String title, String createdDate,
                   String lastModificationDate, int noteType, int category, int priority, String deadline) {
        this.noteId = noteId;
        this.title = title;
        this.createdDate = createdDate;
        this.lastModificationDate = lastModificationDate;
        this.noteType = noteType;
        this.category = category;
        this.priority = priority;
        this.deadline = deadline;
    }

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModificationDate() {
        return lastModificationDate;
    }

    public void setLastModificationDate(String lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getNoteType() {
        return noteType;
    }

    public void setNoteType(int noteType) {
        this.noteType = noteType;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }
}
