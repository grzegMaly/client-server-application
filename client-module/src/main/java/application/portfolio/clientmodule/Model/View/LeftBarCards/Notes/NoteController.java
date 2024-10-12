package application.portfolio.clientmodule.Model.View.LeftBarCards.Notes;

import application.portfolio.clientmodule.Model.View.LeftBarCards.Notes.NoteUtils.NoteInfoDialog;
import application.portfolio.clientmodule.OtherElements.NoteDAO;
import application.portfolio.clientmodule.OtherElements.temp.Objects;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;

import java.util.List;

public class NoteController {

    private static ContextMenu lastContextMenu = null;

    public static TableView<NoteDAO> getTableView() {

        TableView<NoteDAO> notesTable = new TableView<>();

        TableColumn<NoteDAO, String> titleCol = new TableColumn<>("Title");
        TableColumn<NoteDAO, String> typeCol = new TableColumn<>("Note Type");
        TableColumn<NoteDAO, String> createdDateCol = new TableColumn<>("Created Date");
        TableColumn<NoteDAO, String> contentCol = new TableColumn<>("Content");

        titleCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        typeCol.setCellValueFactory(cellData -> {
            String val = NoteDAO.getName(cellData.getValue().getNoteType());
            return new SimpleStringProperty(val);
        });
        createdDateCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCreatedDate().toString()));
        contentCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getContent()));

        notesTable.getColumns().addAll(List.of(titleCol, typeCol, createdDateCol, contentCol));
        notesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        List<NoteDAO> notes = loadNotes();
        notesTable.getItems().addAll(notes);
        loadBehavior(notesTable);

        return notesTable;
    }

    private static void loadBehavior(TableView<NoteDAO> notesTable) {

        notesTable.setRowFactory(evt -> {
            TableRow<NoteDAO> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() > 1 &&
                        !row.isEmpty()) {

                    NoteDAO note = row.getItem();
                    NoteInfoDialog infoDialog = new NoteInfoDialog();
                    infoDialog.useDialog(note, NoteInfoDialog.OpenOption.READ);
                } else if (event.getButton().equals(MouseButton.SECONDARY) && !row.isEmpty()) {
                    showContextMenu(row, event.getScreenX(), event.getScreenY());
                }
            });
            return row;
        });
    }

    private static void showContextMenu(TableRow<NoteDAO> row, double xPoint, double yPoint) {

        if (lastContextMenu != null) {
            lastContextMenu.hide();
        }

        ContextMenu contextMenu = getContextMenu(row);
        lastContextMenu = contextMenu;

        contextMenu.show(row, xPoint, yPoint);
        contextMenu.setOnHidden(evt -> lastContextMenu.hide());
    }

    private static ContextMenu getContextMenu(TableRow<NoteDAO> row) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem details = new MenuItem("Details");
        MenuItem edit = new MenuItem("Edit");
        MenuItem delete = new MenuItem("Delete");

        NoteDAO note = row.getItem();
        details.setOnAction(evt -> {
            NoteInfoDialog infoDialog = new NoteInfoDialog();
            infoDialog.useDialog(note, NoteInfoDialog.OpenOption.READ);
        });

        edit.setOnAction(evt -> {
            NoteInfoDialog infoDialog = new NoteInfoDialog();
            infoDialog.useDialog(note, NoteInfoDialog.OpenOption.WRITE);
        });

        delete.setOnAction(evt -> {
            System.out.println("Deleting: " + note);
        });

        contextMenu.getItems().addAll(details, edit, delete);
        return contextMenu;
    }

    public static List<NoteDAO> loadNotes() {

        return Objects.loadNotes();
    }
}
