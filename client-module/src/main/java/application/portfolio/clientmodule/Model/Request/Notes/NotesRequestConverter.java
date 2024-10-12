package application.portfolio.clientmodule.Model.Request.Notes;

import application.portfolio.clientmodule.Model.Request.Notes.NoteRequest.BaseNoteRequest;
import application.portfolio.clientmodule.Model.Request.Notes.NoteRequest.DeadlineNoteRequest;
import application.portfolio.clientmodule.Model.Request.Notes.NoteRequest.RegularNoteRequest;
import application.portfolio.clientmodule.OtherElements.NoteDAO;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class NotesRequestConverter {

    private final Map<NoteDAO.NoteType, Function<NoteDAO, BaseNoteRequest<?>>> convertors = new HashMap<>();

    {
        convertors.put(NoteDAO.NoteType.REGULAR_NOTE, this::regularNoteRequest);
        convertors.put(NoteDAO.NoteType.DEADLINE_NOTE, this::deadlineNoteRequest);
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

    private RegularNoteRequest regularNoteRequest(NoteDAO noteDAO) {

        return new RegularNoteRequest(noteDAO.getTitle(), noteDAO.getNoteType(), noteDAO.getContent())
        .withCategory(noteDAO.getCategory())
        .withCreatedDate(noteDAO.getCreatedDate());
    }

    private DeadlineNoteRequest deadlineNoteRequest(NoteDAO noteDAO) {

        return new DeadlineNoteRequest(noteDAO.getTitle(), noteDAO.getNoteType(), noteDAO.getContent())
                .withPriority(noteDAO.getPriority())
                .withDeadline(noteDAO.getDeadline())
                .withCreatedDate(noteDAO.getCreatedDate());
    }
}
