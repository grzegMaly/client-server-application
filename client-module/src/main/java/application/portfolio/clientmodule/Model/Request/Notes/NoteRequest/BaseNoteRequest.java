package application.portfolio.clientmodule.Model.Request.Notes.NoteRequest;

import application.portfolio.clientmodule.Model.Model.Notes.NoteDAO;
import application.portfolio.clientmodule.utils.DateUtils;

import java.time.LocalDateTime;

public abstract class BaseNoteRequest<T extends BaseNoteRequest<T>> {

    private String title;
    private NoteDAO.NoteType type;
    private String createdDate;
    private String content;

    public BaseNoteRequest(String title, NoteDAO.NoteType type, String content) {
        this.title = title;
        this.type = type;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public NoteDAO.NoteType getType() {
        return type;
    }

    public void setType(NoteDAO.NoteType type) {
        this.type = type;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public T withCreatedDate(LocalDateTime createdDate) {
        this.createdDate = DateUtils.formatCreatedDate(createdDate);
        return (T) this;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "BaseNoteRequest{" +
                "title='" + title + '\'' +
                ", type=" + type +
                ", createdDate=" + createdDate +
                ", content='" + content + '\'' +
                '}';
    }
}
