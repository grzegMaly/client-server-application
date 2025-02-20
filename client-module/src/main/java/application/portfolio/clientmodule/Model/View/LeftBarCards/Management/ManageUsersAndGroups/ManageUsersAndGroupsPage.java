package application.portfolio.clientmodule.Model.View.LeftBarCards.Management.ManageUsersAndGroups;

import application.portfolio.clientmodule.Model.Model.Person.Person;
import application.portfolio.clientmodule.Model.View.LeftBarCards.Management.ManagementBinder;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.util.List;

public class ManageUsersAndGroupsPage extends VBox {

    private final ManageUsersAndGroupsBinder binder = new ManageUsersAndGroupsBinder();
    private final TableView<Person> usersTable;

    {
        usersTable = binder.getTable();
        loadUsersList();
    }


    public ManageUsersAndGroupsPage() {

        Button assignUserBtn = new Button("Assign User to Group");
        Button removeUserFromGroupBtn = new Button("Remove User from Group");
        Button refreshBtn = new Button("Refresh View");

        this.getChildren().addAll(usersTable, assignUserBtn, removeUserFromGroupBtn, refreshBtn);
        binder.bindControls(assignUserBtn, removeUserFromGroupBtn, refreshBtn);
    }

    private void loadUsersList() {
        List<Person> usersList = ManagementBinder.getUsers();
        usersTable.getItems().addAll(usersList);
    }
}
