package application.portfolio.clientmodule.Model.View.LeftBarCards.Notes;

import application.portfolio.clientmodule.Model.View.LeftBarCards.Notes.NoteUtils.NoteBinder;
import application.portfolio.clientmodule.Model.View.LeftBarCards.Notes.NoteUtils.NoteInfoDialog;
import application.portfolio.clientmodule.Model.Model.Notes.Note;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;

import java.util.List;

public class NoteController {

    private NoteBinder noteBinder;
    private static ContextMenu lastContextMenu;
    private TableView<Note> notesTable;

    public NoteController() {}

    public TableView<Note> getTableView() {

        notesTable = new TableView<>();

        TableColumn<Note, String> titleCol = new TableColumn<>("Title");
        TableColumn<Note, String> typeCol = new TableColumn<>("Note Type");
        TableColumn<Note, String> createdDateCol = new TableColumn<>("Created Date");

        titleCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        typeCol.setCellValueFactory(cellData -> {
            String val = Note.getName(cellData.getValue().getNoteType());
            return new SimpleStringProperty(val);
        });
        createdDateCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCreatedDate().toString()));

        notesTable.getColumns().addAll(List.of(titleCol, typeCol, createdDateCol));
        notesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        loadBehavior(notesTable);
        return notesTable;
    }

    private void loadBehavior(TableView<Note> notesTable) {

        notesTable.setRowFactory(evt -> {
            TableRow<Note> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() > 1 &&
                        !row.isEmpty()) {

                    Note note = row.getItem();
                    NoteInfoDialog infoDialog = new NoteInfoDialog(note, notesTable);
                    infoDialog.useDialog();
                } else if (event.getButton().equals(MouseButton.SECONDARY) && !row.isEmpty()) {
                    showContextMenu(row, event.getScreenX(), event.getScreenY());
                }
            });
            return row;
        });
    }

    private void showContextMenu(TableRow<Note> row, double xPoint, double yPoint) {

        if (lastContextMenu != null) {
            lastContextMenu.hide();
        }

        ContextMenu contextMenu = getContextMenu(row);
        lastContextMenu = contextMenu;

        contextMenu.show(row, xPoint, yPoint);
        contextMenu.setOnHidden(evt -> lastContextMenu.hide());
    }

    private ContextMenu getContextMenu(TableRow<Note> row) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem delete = new MenuItem("Delete");

        Note note = row.getItem();

        delete.setOnAction(evt -> delete(note));
        contextMenu.getItems().addAll(delete);
        return contextMenu;
    }

    private void delete(Note note) {

        boolean result = noteBinder.delete(note.getNoteId());
        if (result) {
            notesTable.getItems().remove(note);
        }
    }

    public void setNoteBinder(NoteBinder noteBinder) {
        this.noteBinder = noteBinder;
    }
}
