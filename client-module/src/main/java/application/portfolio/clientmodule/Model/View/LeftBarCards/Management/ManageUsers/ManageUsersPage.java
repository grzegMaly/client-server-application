package application.portfolio.clientmodule.Model.View.LeftBarCards.Management.ManageUsers;

import application.portfolio.clientmodule.Model.Model.Person.Person;
import application.portfolio.clientmodule.Model.View.LeftBarCards.Management.ManagementBinder;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.util.List;

public class ManageUsersPage extends VBox {

    private final ManageUsersBinder binder = new ManageUsersBinder();
    private final ButtonBar buttonBar = new ButtonBar();

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

        buttonBar.getButtons().addAll(addUserBtn, editUserBtn, removeUserBtn, reloadBtn);

        this.getChildren().addAll(usersTable, buttonBar);
        binder.bindControls(addUserBtn, removeUserBtn, editUserBtn, reloadBtn);

        loadStyles();
    }

    private void loadStyles() {
        Platform.runLater(() -> {
            this.setSpacing(10);
            buttonBar.getButtons().forEach(e -> e.getStyleClass().add("managementBtn"));
            usersTable.getStyleClass().add("managementDialogTableView");
        });
    }

    private void loadUsersList() {
        List<Person> usersList = ManagementBinder.getUsers();
        usersTable.getItems().addAll(usersList);
    }
}
