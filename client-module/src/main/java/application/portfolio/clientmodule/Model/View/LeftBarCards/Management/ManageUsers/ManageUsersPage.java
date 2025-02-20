package application.portfolio.clientmodule.Model.View.LeftBarCards.Management.ManageUsers;

import application.portfolio.clientmodule.Model.Model.Person.Person;
import application.portfolio.clientmodule.Model.View.LeftBarCards.Management.ManagementBinder;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.util.List;

public class ManageUsersPage extends VBox {

    private final ManageUsersBinder binder = new ManageUsersBinder();

    {
        usersTable = binder.getTable();
        loadUsersList();
    }

    private final TableView<Person> usersTable;

    public ManageUsersPage() {

        Button addUserBtn = new Button("Add User");
        Button editUserBtn = new Button("Edit User");
        Button removeUserBtn = new Button("Remove User");
        Button reloadBtn = new Button("Reload List");

        this.getChildren().addAll(usersTable, addUserBtn, removeUserBtn, editUserBtn, reloadBtn);
        binder.bindControls(addUserBtn, removeUserBtn, editUserBtn, reloadBtn);
    }

    private void loadUsersList() {
        List<Person> usersList = ManagementBinder.getUsers();
        usersTable.getItems().addAll(usersList);
    }
}
