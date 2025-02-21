package application.portfolio.clientmodule.Model.View.LeftBarCards.Management.ManageUsers;

import application.portfolio.clientmodule.Model.Model.Person.Person;
import application.portfolio.clientmodule.Model.Model.Person.PersonDAO;
import application.portfolio.clientmodule.Model.Request.Management.Users.ManageUsersViewModel;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class UserFormStage {

    //Tools
    private Scene scene;
    private final Stage stage = new Stage(StageStyle.UTILITY);
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
    private final GridPane grid = new GridPane();

    private final Label fNameLbl = new Label("First Name:");
    private final TextField firstNameField = new TextField();

    private final Label lNameLbl = new Label("Last Name:");
    private final TextField lastNameField = new TextField();

    private final Label roleLbl = new Label("Role:");
    private final ChoiceBox<Pair<String, Integer>> roleBox = new ChoiceBox<>();

    private Label emailLbl;
    private Label passwordLbl;
    private TextField emailTf;
    private TextField passwordTf;

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

        scene = new Scene(grid, 300, 250);
        loadStyles();

        Platform.runLater(() -> {
            stage.setScene(scene);
            stage.show();
        });
    }

    private void initBaseElements() {
        initChoice();

        firstNameField.textProperty().bindBidirectional(firstName);
        lastNameField.textProperty().bindBidirectional(lastName);

        Platform.runLater(() -> {
            grid.add(fNameLbl, 0, 0);
            grid.add(firstNameField, 1, 0);
            grid.add(lNameLbl, 0, 1);
            grid.add(lastNameField, 1, 1);
            grid.add(roleLbl, 0, 4);
            grid.add(roleBox, 1, 4);
            grid.add(saveBtn, 0, 5);
            grid.add(cancelBtn, 1, 5);
        });

        if (person != null) {
            Platform.runLater(() ->
                    roleBox.getItems().stream()
                    .filter(pair -> pair.getValue().equals(person.getRole().getId()))
                    .findFirst()
                    .ifPresent(roleBox::setValue));
        }
    }

    private void initAdditionalElements() {
        emailLbl = new Label("Email:");
        emailTf = new TextField();
        emailTf.textProperty().bindBidirectional(email);

        passwordLbl = new Label("Password:");
        passwordTf = new PasswordField();
        passwordTf.textProperty().bindBidirectional(password);

        Platform.runLater(() -> {
            grid.add(emailLbl, 0, 2);
            grid.add(emailTf, 1, 2);
            grid.add(passwordLbl, 0, 3);
            grid.add(passwordTf, 1, 3);
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

    private void loadStyles() {

        URL resource = getClass().getResource("/View/Styles/Dialogs/Management/ManagementDialog.css");
        if (resource == null) {
            return;
        }

        scene.getStylesheets().add(resource.toExternalForm());
        Platform.runLater(() -> {
            grid.getStyleClass().add("managementDialogGp");
            List<Node> labels = new ArrayList<>(List.of(fNameLbl, lNameLbl, roleLbl));
            List<Node> textFields = new ArrayList<>(List.of(firstNameField, lastNameField));
            List.of(saveBtn, cancelBtn).forEach(e -> e.getStyleClass().add("managementDialogBtn"));

            if (person == null) {
                labels.addAll(List.of(emailLbl, passwordLbl));
                textFields.addAll(List.of(emailTf, passwordTf));
            }

            labels.forEach(e -> e.getStyleClass().add("managementDialogLbl"));
            textFields.forEach(e -> e.getStyleClass().add("managementDialogTf"));

            roleBox.getStyleClass().add("managementDialogChoiceBox");
        });
    }
}
