package application.portfolio.clientmodule.Model.View.LeftBarCards.Notes;

import application.portfolio.clientmodule.Model.Request.Notes.NotesRequestViewModel;
import application.portfolio.clientmodule.Model.View.LeftBarCards.Notes.NoteUtils.NoteBinder;
import application.portfolio.clientmodule.Model.View.LeftBarCards.Notes.NoteUtils.NoteInfoDialog;
import application.portfolio.clientmodule.Model.Model.Notes.NoteDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;

import java.util.List;
import java.util.UUID;

public class NoteController {

    private NotesRequestViewModel viewModel;
    private NoteBinder noteBinder;
    private static ContextMenu lastContextMenu;

    public NoteController() {}

    public TableView<NoteDAO> getTableView() {

        TableView<NoteDAO> notesTable = new TableView<>();

        TableColumn<NoteDAO, String> titleCol = new TableColumn<>("Title");
        TableColumn<NoteDAO, String> typeCol = new TableColumn<>("Note Type");
        TableColumn<NoteDAO, String> createdDateCol = new TableColumn<>("Created Date");

        titleCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        typeCol.setCellValueFactory(cellData -> {
            String val = NoteDAO.getName(cellData.getValue().getNoteType());
            return new SimpleStringProperty(val);
        });
        createdDateCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCreatedDate().toString()));

        notesTable.getColumns().addAll(List.of(titleCol, typeCol, createdDateCol));
        notesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        loadBehavior(notesTable);
        return notesTable;
    }

    private void loadBehavior(TableView<NoteDAO> notesTable) {

        notesTable.setRowFactory(evt -> {
            TableRow<NoteDAO> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() > 1 &&
                        !row.isEmpty()) {

                    NoteDAO note = row.getItem();
                    NoteInfoDialog infoDialog = new NoteInfoDialog();
                    infoDialog.useDialog(note, NoteInfoDialog.OpenOption.READ, noteBinder);
                } else if (event.getButton().equals(MouseButton.SECONDARY) && !row.isEmpty()) {
                    showContextMenu(row, event.getScreenX(), event.getScreenY());
                }
            });
            return row;
        });
    }

    private void showContextMenu(TableRow<NoteDAO> row, double xPoint, double yPoint) {

        if (lastContextMenu != null) {
            lastContextMenu.hide();
        }

        ContextMenu contextMenu = getContextMenu(row);
        lastContextMenu = contextMenu;

        contextMenu.show(row, xPoint, yPoint);
        contextMenu.setOnHidden(evt -> lastContextMenu.hide());
    }

    private ContextMenu getContextMenu(TableRow<NoteDAO> row) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem details = new MenuItem("Details");
        MenuItem edit = new MenuItem("Edit");
        MenuItem delete = new MenuItem("Delete");

        NoteDAO note = row.getItem();
        details.setOnAction(evt -> {
            NoteInfoDialog infoDialog = new NoteInfoDialog();
            infoDialog.useDialog(note, NoteInfoDialog.OpenOption.READ, noteBinder);
        });

        edit.setOnAction(evt -> {
            NoteInfoDialog infoDialog = new NoteInfoDialog();
            infoDialog.useDialog(note, NoteInfoDialog.OpenOption.WRITE, noteBinder);
        });

        delete.setOnAction(evt -> noteBinder.delete(note));

        contextMenu.getItems().addAll(details, edit, delete);
        return contextMenu;
    }

    public List<NoteDAO> loadNotes() {
        return viewModel.loadNotes();
    }

    public void setViewModel(NotesRequestViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public void setNoteBinder(NoteBinder noteBinder) {
        this.noteBinder = noteBinder;
    }

    public String loadNoteContent(UUID noteId) {
        return viewModel.loadNoteContent(noteId);
    }
}
