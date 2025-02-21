package application.portfolio.clientmodule.Model.View.LeftBarCards.Management.ManageGroups;

import application.portfolio.clientmodule.Model.Model.Group.Group;
import application.portfolio.clientmodule.Model.Model.Person.Person;
import application.portfolio.clientmodule.Model.Request.Management.Groups.ManageGroupsViewModel;
import application.portfolio.clientmodule.Model.View.LeftBarCards.Management.ManagementBinder;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.List;


public class GroupFormStage {

    private Scene scene;
    private final Stage stage = new Stage(StageStyle.UTILITY);
    private final GridPane grid = new GridPane();

    private final ManageGroupsViewModel viewModel;
    private final ManageGroupsBinder manageGroupsBinder;

    private final Label groupNameLbl = new Label("Group Name:");
    private final TextField groupNameTf = new TextField();

    private final Label ownerLbl = new Label("Owner:");
    private final TextField ownerTf = new TextField();

    private final Button selectOwnerBtn = new Button("≡");

    private final Button saveBtn = new Button("Save");
    private final Button cancelBtn = new Button("Cancel");

    private final StringProperty groupName = new SimpleStringProperty();
    private final StringProperty ownerName = new SimpleStringProperty();
    private final ObjectProperty<Person> selectedOwner = new SimpleObjectProperty<>();


    public GroupFormStage(ManageGroupsViewModel viewModel, ManageGroupsBinder manageGroupsBinder) {
        this.viewModel = viewModel;
        this.manageGroupsBinder = manageGroupsBinder;
    }

    public void openGroupForm(Group existingGroup) {
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(existingGroup == null ? "Add New Group" : "Edit Group");

        scene = new Scene(grid, 350, 200);
        grid.setHgap(10);
        grid.setVgap(10);

        groupNameTf.textProperty().bindBidirectional(groupName);

        ownerTf.textProperty().bind(ownerName);
        ownerTf.setDisable(true);


        selectOwnerBtn.setOnAction(evt -> openUserSelectionDialog());

        grid.add(groupNameLbl, 0, 0);
        grid.add(groupNameTf, 1, 0);

        grid.add(ownerLbl, 0, 1);
        grid.add(ownerTf, 1, 1);
        grid.add(selectOwnerBtn, 2, 1);

        grid.add(saveBtn, 0, 2);
        grid.add(cancelBtn, 1, 2);

        if (existingGroup != null) {
            groupName.set(existingGroup.getGroupName());
            Person owner = existingGroup.getOwner();
            if (owner != null) {
                ownerName.set(owner.getName());
                selectedOwner.set(owner);
            }
        }

        saveBtn.setOnAction(evt -> {
            String name = groupName.get();
            Person owner = selectedOwner.get();

            if (name == null || name.isBlank() || owner == null) {
                new Alert(Alert.AlertType.WARNING, "Please provide all details").showAndWait();
                return;
            }

            Group group;
            if (existingGroup != null) {
                group = new Group(existingGroup.getGroupId(), name, owner);
            } else {
                group = new Group(name, owner);
            }

            Group returnedGroup = existingGroup == null
                    ? viewModel.addGroup(group)
                    : viewModel.updateGroup(group);

            if (returnedGroup != null) {
                manageGroupsBinder.mergeGroupToStructure(returnedGroup, existingGroup);
                Platform.runLater(stage::close);
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to save group").showAndWait();
            }
        });

        cancelBtn.setOnAction(evt -> stage.close());

        stage.setScene(scene);
        loadStyles();

        stage.show();
    }

    private void openUserSelectionDialog() {
        Stage dialogStage = new Stage(StageStyle.UTILITY);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Select Owner");

        ObservableList<Person> users = FXCollections.observableArrayList(
                ManagementBinder.getUsers().stream().filter(p -> p.getRole().getId() != 0).toList()
        );

        ListView<Person> listView = new ListView<>(users);
        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Person person, boolean empty) {
                super.updateItem(person, empty);
                setText((empty || person == null) ? null : person.getName());
            }
        });

        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Person selectedUser = listView.getSelectionModel().getSelectedItem();
                if (selectedUser != null) {
                    ownerName.set(selectedUser.getName());
                    selectedOwner.set(selectedUser);
                    dialogStage.close();
                }
            }
        });

        VBox vbox = new VBox(listView);
        dialogStage.setScene(new Scene(vbox, 300, 400));
        dialogStage.show();
    }

    private void loadStyles() {

        URL resource = getClass().getResource("/View/Styles/Dialogs/Management/ManagementDialog.css");
        if (resource == null) {
            return;
        }

        scene.getStylesheets().add(resource.toExternalForm());
        Platform.runLater(() -> {

            grid.getStyleClass().add("managementDialogGp");
            List.of(cancelBtn, saveBtn, selectOwnerBtn)
                    .forEach(e -> e.getStyleClass().add("managementDialogBtn"));

            List.of(groupNameLbl, ownerLbl)
                    .forEach(e -> e.getStyleClass().add("managementDialogLbl"));

            List.of(groupNameTf, ownerTf)
                    .forEach(e -> e.getStyleClass().add("managementDialogTf"));
        });
    }
}
