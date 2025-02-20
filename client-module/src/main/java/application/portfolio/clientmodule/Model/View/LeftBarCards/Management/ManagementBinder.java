package application.portfolio.clientmodule.Model.View.LeftBarCards.Management;

import application.portfolio.clientmodule.Model.Model.Group.Group;
import application.portfolio.clientmodule.Model.Model.Person.Person;
import application.portfolio.clientmodule.Model.Request.Management.ManagementViewModel;
import application.portfolio.clientmodule.Model.View.LeftBarCards.Management.ManageGroups.ManageGroupsPage;
import application.portfolio.clientmodule.Model.View.LeftBarCards.Management.ManageUsers.ManageUsersPage;
import application.portfolio.clientmodule.Model.View.LeftBarCards.Management.ManageUsersAndGroups.ManageUsersAndGroupsPage;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import java.util.*;
import java.util.stream.Collectors;

public class ManagementBinder {

    private StackPane managingPagesPane;
    private Button manageUsersBtn;
    private Button manageGroupsBtn;
    private Button manageUsersAndGroupsBtn;

    private ManageUsersPage usersPage;
    private ManageGroupsPage groupsPage;
    private ManageUsersAndGroupsPage usersGroupsPage;

    private static final ManagementViewModel viewModel = new ManagementViewModel();

    private static Map<UUID, Person> allUsers;
    private static Map<UUID, Group> allGroups;

    public void setManagingPagesPane(StackPane managingPagesPane) {
        this.managingPagesPane = managingPagesPane;
    }

    public void setManageUsersBtn(Button manageUsersBtn) {
        this.manageUsersBtn = manageUsersBtn;
    }

    public void setManageGroupsBtn(Button manageGroupsBtn) {
        this.manageGroupsBtn = manageGroupsBtn;
    }

    public void setMangeUsersAndGroupsBtn(Button manageUsersAndGroupsBtn) {
        this.manageUsersAndGroupsBtn = manageUsersAndGroupsBtn;
    }

    public void initialize() {

        manageUsersBtn.setOnAction(e -> {
            if (usersPage == null) {
                usersPage = new ManageUsersPage();
                managingPagesPane.getChildren().add(usersPage);
            }
            showPage(usersPage);
        });
        manageGroupsBtn.setOnAction(e -> {
            if (groupsPage == null) {
                groupsPage = new ManageGroupsPage();
                managingPagesPane.getChildren().add(groupsPage);
            }
            showPage(groupsPage);
        });
        manageUsersAndGroupsBtn.setOnAction(e -> {
            if (usersGroupsPage == null) {
                usersGroupsPage = new ManageUsersAndGroupsPage();
                managingPagesPane.getChildren().add(usersGroupsPage);
            }
            showPage(usersGroupsPage);
        });
    }

    public static List<Person> getUsers() {
        if (allUsers == null) {
            allUsers = new HashMap<>();
            loadUsers();
        }
        return new ArrayList<>(allUsers.values());
    }

    public static void loadUsers() {
        List<Person> people = viewModel.loadUsers();
        addAllUsers(people);
    }

    public static List<Group> getGroups() {

        if (allGroups == null) {
            allGroups = new HashMap<>();
            loadGroups();
        }
        return new ArrayList<>(allGroups.values());
    }

    public static void loadGroups() {
        List<Group> groups = viewModel.getAllGroups();
        addAllGroups(groups);
    }

    private void showPage(Parent page) {
        managingPagesPane.getChildren().forEach(p -> p.setVisible(false));
        page.setVisible(true);
    }

    public static Person getUser(UUID userId) {

        if (allUsers == null) {
            allUsers = new HashMap<>();
        }
        return allUsers.get(userId);
    }

    public static void addUser(Person person) {

        if (allUsers == null) {
            allUsers = new HashMap<>();
        }
        allUsers.put(person.getUserId(), person);
    }

    public static void addAllUsers(Collection<Person> people) {
        allUsers.clear();
        allUsers.putAll(people.stream()
                .collect(Collectors.toMap(Person::getUserId, person -> person)));
    }

    public static void removeUser(Person person) {
        allUsers.remove(person.getUserId());
    }

    public static Group getGroup(UUID groupId) {

        if (allGroups == null) {
            allGroups = new HashMap<>();
        }
        return allGroups.get(groupId);
    }

    public static void addGroup(Group group) {

        if (allGroups == null) {
            allGroups = new HashMap<>();
        }
        allGroups.put(group.getGroupId(), group);
    }

    public static void addAllGroups(Collection<Group> groups) {
        allGroups.clear();
        allGroups.putAll(groups.stream()
                .collect(Collectors.toMap(Group::getGroupId, group -> group)));
    }

    public static void removeGroup(Group group) {
        allGroups.remove(group.getGroupId());
    }
}
