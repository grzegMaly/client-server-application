package application.portfolio.clientmodule.Model.Request.Notes.NoteRequest;

import java.util.UUID;

public class NoteRequest {

    private UUID userId;
    private UUID noteId;

    public NoteRequest(UUID userId) {
        this(userId, null);
    }

    public NoteRequest(UUID userId, UUID noteId) {
        this.userId = userId;
        this.noteId = noteId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getNoteId() {
        return noteId;
    }

    public void setNoteId(UUID noteId) {
        this.noteId = noteId;
    }
}
