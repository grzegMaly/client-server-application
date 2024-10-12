package application.portfolio.clientmodule.Model.Request.Notes.NoteRequest;

import application.portfolio.clientmodule.OtherElements.NoteDAO;

public class RegularNoteRequest extends BaseNoteRequest<RegularNoteRequest> {

    private NoteDAO.Category category;

    public RegularNoteRequest(String title, NoteDAO.NoteType type, String content) {
        super(title, type, content);
    }

    public NoteDAO.Category getCategory() {
        return category;
    }

    public RegularNoteRequest withCategory(NoteDAO.Category category) {
        this.category = category;
        return this;
    }

    @Override
    public String toString() {
        return "RegularNoteRequest{" +
                "category=" + category + '\'' +
                "} " + super.toString();
    }
}
