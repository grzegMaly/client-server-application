package application.portfolio.clientmodule.Model.Request.Notes;

import application.portfolio.clientmodule.Connection.UserSession;
import application.portfolio.clientmodule.Model.Model.Notes.NoteType;
import application.portfolio.clientmodule.Model.Request.Notes.NoteRequest.BaseNoteRequest;
import application.portfolio.clientmodule.Model.Request.Notes.NoteRequest.NoteRequest;
import application.portfolio.clientmodule.Model.Model.Notes.NoteDAO;
import application.portfolio.clientmodule.utils.DataParser;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public class NotesRequestConverter {

    private final Map<NoteType, Function<NoteDAO, BaseNoteRequest<?>>> convertors = new HashMap<>();

    /*{
        convertors.put(NoteType.REGULAR_NOTE, this::regularNoteRequest);
        convertors.put(NoteType.DEADLINE_NOTE, this::deadlineNoteRequest);
    }*/

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

    public BaseNoteRequest<?> toNoteRequest(NoteDAO noteDAO) {

        Function<NoteDAO, BaseNoteRequest<?>> converter = convertors.get(noteDAO.getNoteType());

        if (converter == null) {
            //Todo: To improve
//            throw new IllegalAccessException("Nie ma takiej lambdy");
            System.out.println("Coś jest nie tak");
        }

        assert converter != null;
        return converter.apply(noteDAO);
    }

    public NoteRequest convertToLoadRequest() {
        UUID id = UserSession.getInstance().getLoggedInUser().getId();
        return new NoteRequest(id);
    }

    public NoteRequest convertToNoteContentRequest(UUID noteId) {
        UUID id = UserSession.getInstance().getLoggedInUser().getId();;
        return new NoteRequest(id, noteId);
    }

    /*private RegularNoteRequest regularNoteRequest(NoteDAO noteDAO) {

        return new RegularNoteRequest(noteDAO.getTitle(), noteDAO.getNoteType(), noteDAO.getContent())
        .withCategory(noteDAO.getCategory())
        .withCreatedDate(noteDAO.getCreatedDate());
    }

    private DeadlineNoteRequest deadlineNoteRequest(NoteDAO noteDAO) {

        return new DeadlineNoteRequest(noteDAO.getTitle(), noteDAO.getNoteType(), noteDAO.getContent())
                .withPriority(noteDAO.getPriority())
                .withDeadline(noteDAO.getDeadline())
                .withCreatedDate(noteDAO.getCreatedDate());
    }*/
}
