package application.portfolio.clientmodule.Model.View.LeftBarCards.Management.ManageUsersAndGroups;

import application.portfolio.clientmodule.Model.Model.Group.Group;
import application.portfolio.clientmodule.Model.Model.Person.Person;
import application.portfolio.clientmodule.Model.Request.Management.UserAndGroups.ManageUsersAndGroupsViewModel;
import application.portfolio.clientmodule.Model.View.LeftBarCards.Management.ManagementBinder;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ManageUsersAndGroupsBinder {

    private TableView<Person> personTable;
    private final ManageUsersAndGroupsViewModel viewModel = new ManageUsersAndGroupsViewModel();

    public void bindControls(Button assignUserBtn, Button removeUserFromGroupBtn, Button refreshBtn) {

        bindAssignBtn(assignUserBtn);
        bindRemoveBtn(removeUserFromGroupBtn);
        bindRefreshBtn(refreshBtn);
    }

    private void bindAssignBtn(Button assignUserBtn) {
        assignUserBtn.setOnAction(evt -> {

            Person person = personTable.getSelectionModel().getSelectedItem();
            if (person == null) {
                new Alert(Alert.AlertType.WARNING, "Please Select User").show();
                return;
            }
            UserGroupAssignmentDialog dialog = new UserGroupAssignmentDialog(person, true, viewModel);
            dialog.show();
        });
    }

    private void bindRemoveBtn(Button removeUserFromGroupBtn) {
        removeUserFromGroupBtn.setOnAction(evt -> {

            Person person = personTable.getSelectionModel().getSelectedItem();
            if (person == null) {
                new Alert(Alert.AlertType.WARNING, "Please Select User").show();
                return;
            }
            UserGroupAssignmentDialog dialog = new UserGroupAssignmentDialog(person, false, viewModel);
            dialog.show();
        });
    }

    private void bindRefreshBtn(Button refreshBtn) {
        refreshBtn.setOnAction(e -> Platform.runLater(() -> personTable.refresh()));
    }

    public TableView<Person> getTable() {

        personTable = new TableView<>();

        TableColumn<Person, String> nameCol = new TableColumn<>("User Name");
        TableColumn<Person, String> groupsCol = new TableColumn<>("Groups");
        TableColumn<Person, String> ownerCol = new TableColumn<>("Owner Name");

        nameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));

        groupsCol.setCellValueFactory(cellData -> {

            Person person = cellData.getValue();
            List<Group> groupsList = person.getJoinedGroups();

            SimpleStringProperty groupTextProperty = new SimpleStringProperty("Loading...");

            if (groupsList != null && !groupsList.isEmpty()) {
                String groups = groupsList.stream().map(Group::getGroupName).collect(Collectors.joining(", "));
                groupTextProperty.set(groups);
            } else if (!person.isJoinedGroupsChecked()) {

                person.setJoinedGroupChecked(true);
                CompletableFuture.supplyAsync(() -> getJoinedGroups(person.getUserId()))
                        .thenAccept(joinedGroups -> Platform.runLater(() -> {

                            if (joinedGroups == null || joinedGroups.isEmpty()) {
                                groupTextProperty.set("No Groups");
                                return;
                            }

                            Person owner = ManagementBinder.getUser(joinedGroups.get(0).getOwnerId());

                            joinedGroups.forEach(g -> {
                                Group existingGroup = ManagementBinder.getGroup(g.getGroupId());
                                if (existingGroup == null) {
                                    owner.addOwnedGroup(g);
                                    ManagementBinder.addGroup(g);
                                    existingGroup = g;
                                }
                                person.addJoinedGroup(existingGroup);
                            });

                            String updatedGroups = joinedGroups.stream()
                                    .map(Group::getGroupName)
                                    .collect(Collectors.joining(", "));
                            groupTextProperty.set(updatedGroups);
                            cellData.getTableView().refresh();
                        }));
            } else {
                groupTextProperty.set("No Groups");
            }

            return groupTextProperty;
        });

        ownerCol.setCellValueFactory(cellData -> {
            List<Group> groupsList = cellData.getValue().getJoinedGroups();

            if (groupsList == null || groupsList.isEmpty()) {
                return new SimpleStringProperty("");
            }

            Person owner = groupsList.get(0).getOwner();
            return new SimpleStringProperty(owner != null ? owner.getName() : "Unknown Owner");
        });

        personTable.getColumns().addAll(List.of(nameCol, groupsCol, ownerCol));
        personTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return personTable;
    }

    private List<Group> getJoinedGroups(UUID userId) {
        return viewModel.getJoinedGroups(userId);
    }
}
