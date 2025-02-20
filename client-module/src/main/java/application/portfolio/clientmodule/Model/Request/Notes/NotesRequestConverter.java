package application.portfolio.clientmodule.Model.Request.Notes;

import application.portfolio.clientmodule.Connection.UserSession;
import application.portfolio.clientmodule.Model.Request.Notes.NoteRequest.NoteRequest;
import application.portfolio.clientmodule.utils.DataParser;

import java.util.Map;
import java.util.UUID;

public class NotesRequestConverter {

    public static String toRegularUserQuery(NoteRequest noteRequest) {
        String userId = noteRequest.getUserId().toString();
        return DataParser.paramsString(Map.of("userId", userId));
    }

    public static String toQueryLoadParams(NoteRequest noteRequest) {
        String userId = noteRequest.getUserId().toString();
        return DataParser.paramsString(Map.of("userId", userId, "option", "list-all"));
    }

    public static String toQueryContentRequest(NoteRequest noteRequest) {
        String userId = noteRequest.getUserId().toString();
        String noteId = noteRequest.getNoteId().toString();
        return DataParser.paramsString(Map.of("userId", userId,
                "option", "content",
                "noteId", noteId));
    }

    public static String toQueryWithNoteId(NoteRequest noteRequest) {
        String userId = noteRequest.getUserId().toString();
        String noteId = noteRequest.getNoteId().toString();
        return DataParser.paramsString(Map.of("userId", userId,
                "noteId", noteId));
    }

    public NoteRequest convertToWithUserIdRequest() {
        UUID id = UserSession.getInstance().getLoggedInUser().getUserId();
        return new NoteRequest(id);
    }

    public NoteRequest convertToWithUIdAndNoteIdRequest(UUID noteId) {
        NoteRequest noteRequest = convertToWithUserIdRequest();
        noteRequest.setNoteId(noteId);
        return noteRequest;
    }
}
