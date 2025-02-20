package application.portfolio.clientmodule.Model.View.LeftBarCards.Management.ManageGroups;

import application.portfolio.clientmodule.Model.Model.Group.Group;
import application.portfolio.clientmodule.Model.View.LeftBarCards.Management.ManagementBinder;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.util.List;

public class ManageGroupsPage extends VBox {

    private final ManageGroupsBinder binder = new ManageGroupsBinder();
    private final TableView<Group> groupsTable;

    {
        groupsTable = binder.getTable();
        loadGroupsList();
    }


    public ManageGroupsPage() {
        Button addGroupBtn = new Button("Add Group");
        Button removeGroupBtn = new Button("Remove Group");
        Button editGroupBtn = new Button("Edit Group");
        Button reloadBtn = new Button("Reload List");
        this.getChildren().addAll(groupsTable, addGroupBtn, removeGroupBtn, editGroupBtn, reloadBtn);
        binder.bindControls(addGroupBtn, removeGroupBtn, editGroupBtn, reloadBtn);
    }

    private void loadGroupsList() {
        List<Group> groupsList = ManagementBinder.getGroups();
        groupsTable.getItems().addAll(groupsList);
    }
}
