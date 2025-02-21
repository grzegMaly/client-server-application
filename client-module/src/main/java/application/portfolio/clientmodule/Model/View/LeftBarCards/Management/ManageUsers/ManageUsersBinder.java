package application.portfolio.clientmodule.Model.View.LeftBarCards.Management.ManageUsers;

import application.portfolio.clientmodule.Connection.UserSession;
import application.portfolio.clientmodule.Model.Model.Person.Person;
import application.portfolio.clientmodule.Model.Request.Management.Users.ManageUsersViewModel;
import application.portfolio.clientmodule.Model.View.LeftBarCards.Management.ManagementBinder;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

public class ManageUsersBinder {

    private TableView<Person> personTable;
    private final ManageUsersViewModel viewModel = new ManageUsersViewModel();

    public void bindControls(Button addUserBtn, Button removeUserBtn, Button editUserBtn, Button reloadBtn) {
        bindAddBtn(addUserBtn);
        bindRemoveBtn(removeUserBtn);
        bindEditBtn(editUserBtn);
        bindReloadBtn(reloadBtn);
    }

    public TableView<Person> getTable() {

        personTable = new TableView<>();
        TableColumn<Person, String> fNameCol = new TableColumn<>("First Name");
        TableColumn<Person, String> lNameCol = new TableColumn<>("Last Name");
        TableColumn<Person, Integer> roleCol = new TableColumn<>("Role");

        fNameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFirstName()));
        lNameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getLastName()));
        roleCol.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getRole().getId()));

        personTable.getColumns().addAll(List.of(fNameCol, lNameCol, roleCol));
        personTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        return personTable;
    }

    private void bindAddBtn(Button addBtn) {
        addBtn.setOnAction(e -> {
            UserFormStage form = new UserFormStage(viewModel, this);
            form.openUserForm(null);
        });
    }

    private void bindRemoveBtn(Button removeBtn) {
        removeBtn.setOnAction(e -> {
            Person personDAO = personTable.getSelectionModel().getSelectedItem();

            if (personDAO == null) {
                return;
            }

            if (personDAO.equals(UserSession.getInstance().getLoggedInUser())) {
                new Alert(Alert.AlertType.ERROR, "Unsupported Operation").show();
                return;
            }

            if (viewModel.removeUser(personDAO)) {
                personTable.getItems().remove(personDAO);
                ManagementBinder.removeUser(personDAO);
            }
        });
    }

    private void bindEditBtn(Button editBtn) {
        editBtn.setOnAction(e -> {
            Person selectedPerson = personTable.getSelectionModel().getSelectedItem();
            if (selectedPerson != null) {
                UserFormStage form = new UserFormStage(viewModel, this);
                form.openUserForm(selectedPerson);
            } else {
                new Alert(Alert.AlertType.WARNING, "Select a user to edit").showAndWait();
            }
        });
    }

    private void bindReloadBtn(Button reloadBtn) {
        reloadBtn.setOnAction(e -> {
            ManagementBinder.loadUsers();
            List<Person> people = ManagementBinder.getUsers();
            personTable.getItems().setAll(people);
        });
    }

    protected void addToTable(Person person) {
        Platform.runLater(() -> {
            personTable.getItems().add(person);
            personTable.refresh();
        });
    }

    protected void mergePersonToStructure(Person returnedPerson, Person existingPerson) {

        if (existingPerson != null) {
            existingPerson.setFirstName(returnedPerson.getFirstName());
            existingPerson.setLastName(returnedPerson.getLastName());
            existingPerson.setRole(returnedPerson.getRole());
            Platform.runLater(personTable::refresh);
        } else {
            ManagementBinder.addUser(returnedPerson);
            addToTable(returnedPerson);
        }
    }
}
