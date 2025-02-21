package application.portfolio.clientmodule.Model.View.LeftBarCards.Management.ManageUsersAndGroups;

import application.portfolio.clientmodule.Model.Model.Person.Person;
import application.portfolio.clientmodule.Model.View.LeftBarCards.Management.ManagementBinder;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.util.List;

public class ManageUsersAndGroupsPage extends VBox {

    private final ManageUsersAndGroupsBinder binder = new ManageUsersAndGroupsBinder();
    private final TableView<Person> usersTable;
    private final ButtonBar buttonBar = new ButtonBar();

    {
        usersTable = binder.getTable();
        loadUsersList();
    }


    public ManageUsersAndGroupsPage() {

        Button assignUserBtn = new Button("Assign User to Group");
        Button removeUserFromGroupBtn = new Button("Remove User from Group");
        Button refreshBtn = new Button("Refresh View");

        buttonBar.getButtons().addAll(assignUserBtn, removeUserFromGroupBtn, refreshBtn);

        this.getChildren().addAll(usersTable, buttonBar);
        binder.bindControls(assignUserBtn, removeUserFromGroupBtn, refreshBtn);

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
