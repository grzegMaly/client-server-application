package application.portfolio.clientmodule.Model.View.LeftBarCards.Management.ManageGroups;

import application.portfolio.clientmodule.Model.Model.Group.Group;
import application.portfolio.clientmodule.Model.Model.Person.Person;
import application.portfolio.clientmodule.Model.Request.Management.Groups.ManageGroupsViewModel;
import application.portfolio.clientmodule.Model.View.LeftBarCards.Management.ManagementBinder;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ManageGroupsBinder {

    private TableView<Group> groupTable;
    private final ManageGroupsViewModel viewModel = new ManageGroupsViewModel();

    public void bindControls(Button addGroupBtn, Button removeGroupBtn, Button editGroupBtn, Button reloadBtn) {

        bindAddGroupBtn(addGroupBtn);
        bindRemoveGroupBtn(removeGroupBtn);
        bindEditGroupBtn(editGroupBtn);
        bindReloadBtn(reloadBtn);
    }

    private void bindAddGroupBtn(Button addGroupBtn) {
        addGroupBtn.setOnAction(evt -> {
            GroupFormStage formStage = new GroupFormStage(viewModel, this);
            formStage.openGroupForm(null);
        });
    }

    private void bindRemoveGroupBtn(Button removeBtn) {
        removeBtn.setOnAction(evt -> {
            Group selectedGroup = groupTable.getSelectionModel().getSelectedItem();
            if (selectedGroup != null && viewModel.removeGroup(selectedGroup.getGroupId())) {
                groupTable.getItems().remove(selectedGroup);
            }
        });
    }

    private void bindEditGroupBtn(Button editGroupBtn) {
        editGroupBtn.setOnAction(evt -> {
            Group selectedGroup = groupTable.getSelectionModel().getSelectedItem();
            if (selectedGroup == null) {
                new Alert(Alert.AlertType.WARNING, "Please select a group to edit").showAndWait();
                return;
            }

            GroupFormStage formStage = new GroupFormStage(viewModel, this);
            formStage.openGroupForm(selectedGroup);
        });
    }

    private void bindReloadBtn(Button reloadBtn) {

        reloadBtn.setOnAction(e -> {
            ManagementBinder.loadGroups();
            List<Group> groups = ManagementBinder.getGroups();
            groupTable.getItems().setAll(groups);
        });
    }

    public TableView<Group> getTable() {

        groupTable = new TableView<>();

        TableColumn<Group, String> nameCol = new TableColumn<>("Group Name");
        TableColumn<Group, String> ownerCol = new TableColumn<>("Owner Name");

        nameCol.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getGroupName()));
        ownerCol.setCellValueFactory(cellData -> {

            Group group = cellData.getValue();
            Person owner = group.getOwner();

            if (owner != null) {
                return new SimpleStringProperty(owner.getName());
            } else {
                CompletableFuture.runAsync(() -> {
                    Person fetchedOwner = fetchOwnerFromServer(group.getOwnerId());
                    if (fetchedOwner != null) {
                        ManagementBinder.addUser(fetchedOwner);
                        group.setOwner(fetchedOwner);
                        fetchedOwner.addOwnedGroup(group);
                        Platform.runLater(() -> cellData.getTableView().refresh());
                    }
                });
                return new SimpleStringProperty("Loading...");
            }
        });

        groupTable.getColumns().addAll(List.of(nameCol, ownerCol));
        groupTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return groupTable;
    }

    private Person fetchOwnerFromServer(UUID userId) {
        return viewModel.getOwner(userId);
    }

    protected void addToTable(Group group) {
        Platform.runLater(() -> {
            groupTable.getItems().add(group);
            groupTable.refresh();
        });
    }

    protected void mergeGroupToStructure(Group returnedGroup, Group existingGroup) {

        UUID ownerId = returnedGroup.getOwnerId();
        Person owner = ManagementBinder.getUser(ownerId);

        if (existingGroup != null) {
            existingGroup.setGroupName(returnedGroup.getGroupName());
            if (!existingGroup.getOwnerId().equals(ownerId)) {
                setOwner(existingGroup, owner);
            }
            Platform.runLater(groupTable::refresh);
        } else {
            ManagementBinder.addGroup(returnedGroup);
            setOwner(returnedGroup, owner);
            addToTable(returnedGroup);
        }
    }

    private void setOwner(Group group, Person owner) {
        group.setOwnerId(owner.getUserId());
        group.setOwner(owner);
        owner.addOwnedGroup(group);
    }
}
