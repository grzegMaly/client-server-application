package application.portfolio.clientmodule.Model.View.LeftBarCards.Management.ManageUsers;

import application.portfolio.clientmodule.Model.Model.Person.Person;
import application.portfolio.clientmodule.Model.Model.Person.PersonDAO;
import application.portfolio.clientmodule.Model.Request.Management.Users.ManageUsersViewModel;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;

public class UserFormStage {

    //Tools
    private Person person;
    private final ManageUsersViewModel viewModel;
    private final ManageUsersBinder manageUsersBinder;

    //Properties
    private final StringProperty firstName = new SimpleStringProperty();
    private final StringProperty lastName = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final StringProperty password = new SimpleStringProperty();
    private final StringProperty role = new SimpleStringProperty();

    //Fields
    private final Stage stage = new Stage(StageStyle.UTILITY);
    private final GridPane grid = new GridPane();
    private final TextField firstNameField = new TextField();
    private final TextField lastNameField = new TextField();
    private final ChoiceBox<Pair<String, Integer>> roleBox = new ChoiceBox<>();
    private final Button saveBtn = new Button("Save");
    private final Button cancelBtn = new Button("Cancel");

    public UserFormStage(ManageUsersViewModel viewModel, ManageUsersBinder manageUsersBinder) {
        this.viewModel = viewModel;
        this.manageUsersBinder = manageUsersBinder;

        grid.setHgap(10);
        grid.setVgap(10);
    }

    public void openUserForm(Person person) {
        this.person = person;

        Platform.runLater(() -> {
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(person == null ? "Add New User" : "Edit User");
        });

        initBaseElements();
        initBtnActions();

        if (person == null) {
            initAdditionalElements();
        } else {
            fillFields();
        }

        Platform.runLater(() -> {
            stage.setScene(new Scene(grid, 300, 250));
            stage.show();
        });
    }

    private void initBaseElements() {
        initChoice();

        firstNameField.textProperty().bindBidirectional(firstName);
        lastNameField.textProperty().bindBidirectional(lastName);

        Platform.runLater(() -> {
            grid.add(new Label("First Name:"), 0, 0);
            grid.add(firstNameField, 1, 0);
            grid.add(new Label("Last Name:"), 0, 1);
            grid.add(lastNameField, 1, 1);
            grid.add(new Label("Role:"), 0, 4);
            grid.add(roleBox, 1, 4);
            grid.add(saveBtn, 0, 5);
            grid.add(cancelBtn, 1, 5);
        });

        if (person != null) {
            Platform.runLater(() -> {
                roleBox.getItems().stream()
                        .filter(pair -> pair.getValue().equals(person.getRole().getId()))
                        .findFirst()
                        .ifPresent(roleBox::setValue);
            });
        }
    }

    private void initAdditionalElements() {
        TextField emailField = new TextField();
        emailField.textProperty().bindBidirectional(email);
        PasswordField passwordField = new PasswordField();
        passwordField.textProperty().bindBidirectional(password);

        Platform.runLater(() -> {
            grid.add(new Label("Email:"), 0, 2);
            grid.add(emailField, 1, 2);
            grid.add(new Label("Password:"), 0, 3);
            grid.add(passwordField, 1, 3);
        });
    }

    private void fillFields() {
        firstName.set(person.getFirstName());
        lastName.set(person.getLastName());
        role.set(person.getRole().name());
    }

    private void initBtnActions() {
        saveBtn.setOnAction(evt -> {
            PersonDAO newPerson = new PersonDAO();
            newPerson.setUserId(person == null ? null : person.getUserId().toString());
            newPerson.setFirstName(firstName.get());
            newPerson.setLastName(lastName.get());
            newPerson.setEmail(person != null ? null : email.get());
            newPerson.setPassword(person != null ? null : password.get());

            Pair<String, Integer> selectedRole = roleBox.getValue();
            if (selectedRole == null) {
                new Alert(Alert.AlertType.ERROR, "Role must be selected!").showAndWait();
                return;
            }
            newPerson.setRole(selectedRole.getValue());
            handleSave(person, newPerson);
        });

        cancelBtn.setOnAction(evt -> stage.close());
    }

    private void handleSave(Person person, PersonDAO newPerson) {

        Person returnedPerson = person == null
                ? viewModel.addUser(newPerson)
                : viewModel.updateUser(newPerson);

        if (returnedPerson != null) {
            manageUsersBinder.mergePersonToStructure(returnedPerson, person);
            Platform.runLater(stage::close);
        } else {
            new Alert(Alert.AlertType.ERROR, "Failed to save user").showAndWait();
        }
    }

    private void initChoice() {

        List<Pair<String, Integer>> list = new ArrayList<>();

        Pair<String, Integer> firstPair = new Pair<>("Employee", 0);
        list.add(new Pair<>("Manager", 1));
        list.add(new Pair<>("Admin", 2));

        roleBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Pair<String, Integer> pair) {
                return pair != null ? pair.getKey() : "";
            }

            @Override
            public Pair<String, Integer> fromString(String string) {
                return roleBox.getItems().stream()
                        .filter(p -> p.getKey().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });

        roleBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                role.set(String.valueOf(newVal.getValue()));
            }
        });

        Platform.runLater(() -> {
            roleBox.getItems().add(firstPair);
            roleBox.getItems().addAll(list);
            roleBox.setValue(firstPair);
        });
    }
}
