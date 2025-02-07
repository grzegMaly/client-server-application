package application.portfolio.clientmodule.Model.Request.Notes.NoteRequest;

import application.portfolio.clientmodule.Model.Model.Notes.NoteDAO;
import application.portfolio.clientmodule.Model.Model.Notes.NoteType;
import application.portfolio.clientmodule.Model.Model.Notes.Priority;

import java.time.LocalDate;

public class DeadlineNoteRequest extends BaseNoteRequest<DeadlineNoteRequest> {

    private Priority priority;
    private LocalDate deadline;

    public DeadlineNoteRequest(String title, NoteType type, String content) {
        super(title, type, content);
    }

    public Priority getPriority() {
        return priority;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public DeadlineNoteRequest withPriority(Priority priority) {
        this.priority = priority;
        return this;
    }

    public DeadlineNoteRequest withDeadline(LocalDate deadline) {
        this.deadline = deadline;
        return this;
    }

    @Override
    public String toString() {
        return "DeadlineNoteRequest{" +
                "priority=" + priority +
                ", deadline=" + deadline + '\'' +
                "} " + super.toString();
    }
}
