package application.portfolio.clientmodule.Model.View.LeftBarCards.Management.ManageUsersAndGroups;

import application.portfolio.clientmodule.Model.Model.Group.Group;
import application.portfolio.clientmodule.Model.Model.Person.Person;
import application.portfolio.clientmodule.Model.Request.Management.UserAndGroups.ManageUsersAndGroupsViewModel;
import application.portfolio.clientmodule.Model.View.LeftBarCards.Management.ManagementBinder;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class UserGroupAssignmentDialog {

    private Scene scene;
    private final Stage stage = new Stage(StageStyle.UTILITY);

    private final Person user;
    private final ObservableList<Group> availableGroups;
    private final boolean isAssignMode;
    private final ListView<Group> groupListView;
    private final ManageUsersAndGroupsViewModel viewModel;

    public UserGroupAssignmentDialog(Person user, boolean isAssignMode, ManageUsersAndGroupsViewModel viewModel) {
        this.user = user;
        this.isAssignMode = isAssignMode;

        this.stage.initModality(Modality.APPLICATION_MODAL);
        this.stage.setTitle(isAssignMode ? "Assign User to Group" : "Remove User from group");

        this.availableGroups = FXCollections.observableArrayList(getInitialGroups());
        this.groupListView = new ListView<>(availableGroups);

        this.viewModel = viewModel;

        setUpListView();
        setUpButtons();
        loadStyles();
    }

    private List<Group> getInitialGroups() {
        if (isAssignMode) {
            if (user.getJoinedGroups() == null || user.getJoinedGroups().isEmpty()) {
                return ManagementBinder.getGroups();
            } else {
                UUID ownerId = user.getJoinedGroups().get(0).getOwnerId();
                return ManagementBinder.getGroups()
                        .stream()
                        .filter(g -> g.getOwnerId().equals(ownerId))
                        .collect(Collectors.toList());
            }
        } else {
            return user.getJoinedGroups();
        }
    }

    private void setUpListView() {
        groupListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Group group, boolean empty) {
                super.updateItem(group, empty);
                if (empty || group == null) {
                    setText(null);
                } else {
                    setText(group.getGroupName());
                }
            }
        });
    }

    private void setUpButtons() {

        Button confirmBtn = new Button(isAssignMode ? "Assign" : "Remove");
        Button cancelBtn = new Button("Cancel");

        confirmBtn.setOnAction(e -> handleAction());
        cancelBtn.setOnAction(e -> stage.close());

        VBox layout = new VBox(10, groupListView, confirmBtn, cancelBtn);
        layout.setPrefSize(350, 400);
        scene = new Scene(layout);

        stage.setScene(scene);
    }

    private void handleAction() {

        Group selectedGroup = groupListView.getSelectionModel().getSelectedItem();
        if (selectedGroup == null) {
            new Alert(Alert.AlertType.WARNING, "Please Select a Group").show();
            return;
        }

        if (isAssignMode) {
            assignUserToGroup(selectedGroup);
        } else {
            removeUserFromGroup(selectedGroup);
        }
    }

    private void assignUserToGroup(Group selectedGroup) {

        if (!viewModel.addUserToGroup(user.getUserId(), selectedGroup.getGroupId())) {
            return;
        }

        UUID ownerId = selectedGroup.getOwnerId();
        user.addJoinedGroup(selectedGroup);

        Set<UUID> joinedGroupIds = user.getJoinedGroups()
                .stream().map(Group::getGroupId)
                .collect(Collectors.toSet());

        List<Group> newFilteredGroups = ManagementBinder.getGroups()
                .stream()
                .filter(group -> group.getOwnerId().equals(ownerId) &&
                        !joinedGroupIds.contains(group.getGroupId()))
                .collect(Collectors.toList());

        availableGroups.setAll(newFilteredGroups);
    }

    private void removeUserFromGroup(Group selectedGroup) {

        if (!viewModel.deleteUserFromGroup(user.getUserId(), selectedGroup.getGroupId())) {
            return;
        }

        user.removeJoinedGroup(selectedGroup);
        availableGroups.remove(selectedGroup);
    }

    private void loadStyles() {

        URL resource = getClass().getResource("/View/Styles/Dialogs/Management/ManagementListDialog.css");
        if (resource == null) {
            return;
        }

        scene.getStylesheets().add(resource.toExternalForm());
        Platform.runLater(() -> {

        });
    }

    public void show() {
        stage.show();
    }
}
