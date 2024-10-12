package application.portfolio.clientmodule.Model.Request.Notes;

import application.portfolio.clientmodule.Model.Request.Notes.NoteRequest.BaseNoteRequest;

public class NoteRequestModel {

    public void save(BaseNoteRequest req) {
        System.out.println("Saving " + req);
    }
}
