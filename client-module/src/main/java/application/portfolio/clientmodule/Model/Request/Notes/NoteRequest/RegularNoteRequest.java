package application.portfolio.clientmodule.Model.Request.Notes.NoteRequest;

import application.portfolio.clientmodule.Model.Model.Notes.Category;
import application.portfolio.clientmodule.Model.Model.Notes.NoteType;

public class RegularNoteRequest extends BaseNoteRequest<RegularNoteRequest> {

    private Category category;

    public RegularNoteRequest(String title, NoteType type, String content) {
        super(title, type, content);
    }

    public Category getCategory() {
        return category;
    }

    public RegularNoteRequest withCategory(Category category) {
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
