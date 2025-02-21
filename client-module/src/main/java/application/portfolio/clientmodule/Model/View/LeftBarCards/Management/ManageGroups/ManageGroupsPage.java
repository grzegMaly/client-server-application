package application.portfolio.clientmodule.Model.View.LeftBarCards.Management.ManageGroups;

import application.portfolio.clientmodule.Model.Model.Group.Group;
import application.portfolio.clientmodule.Model.View.LeftBarCards.Management.ManagementBinder;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.util.List;

public class ManageGroupsPage extends VBox {

    private final ManageGroupsBinder binder = new ManageGroupsBinder();
    private final TableView<Group> groupsTable;
    private final ButtonBar buttonBar = new ButtonBar();

    {
        groupsTable = binder.getTable();
        loadGroupsList();
    }


    public ManageGroupsPage() {
        Button addGroupBtn = new Button("Add Group");
        Button removeGroupBtn = new Button("Remove Group");
        Button editGroupBtn = new Button("Edit Group");
        Button reloadBtn = new Button("Reload List");

        buttonBar.getButtons().addAll(addGroupBtn, removeGroupBtn, editGroupBtn, reloadBtn);
        this.getChildren().addAll(groupsTable, buttonBar);
        binder.bindControls(addGroupBtn, removeGroupBtn, editGroupBtn, reloadBtn);

        loadStyles();
    }

    private void loadStyles() {
        Platform.runLater(() -> {
            this.setSpacing(10);
            buttonBar.getButtons().forEach(e -> e.getStyleClass().add("managementBtn"));
            groupsTable.getStyleClass().add("managementDialogTableView");
        });
    }

    private void loadGroupsList() {
        List<Group> groupsList = ManagementBinder.getGroups();
        groupsTable.getItems().addAll(groupsList);
    }
}
