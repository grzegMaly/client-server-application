package application.portfolio.clientmodule.Model.Model.Person;

import application.portfolio.clientmodule.Model.Model.Group.Group;

import java.util.*;

public class Person {

    private UUID userId;
    private String firstName;
    private String lastName;
    private Role role;
    private String email;
    private String password;
    private Map<UUID, Group> ownedGroups;

    private boolean joinedGroupChecked = false;
    private Map<UUID, Group> joinedGroups;

    public Person() {
    }

    public Person(UUID id) {
        this.userId = id;
    }

    public Person(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public Person(String id, String firstName, String lastName, int role) {
        this.userId = UUID.fromString(id);
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = Role.fromId(role);
    }

    public Person(String firstName, String lastName, int role, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = Role.fromId(role);
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Role getRole() {
        return role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isJoinedGroupsChecked() {
        return joinedGroupChecked;
    }

    public void setJoinedGroupChecked(boolean joinedGroupChecked) {
        this.joinedGroupChecked = joinedGroupChecked;
    }

    public List<Group> getJoinedGroups() {
        return joinedGroups == null ? Collections.emptyList() : new ArrayList<>(joinedGroups.values());
    }


    public void addOwnedGroup(Group group) {

        if (role.getId() < 1) {
            return;
        }

        ownedGroups = Objects.requireNonNullElseGet(ownedGroups, HashMap::new);
        ownedGroups.putIfAbsent(group.getGroupId(), group);
    }

    public void addJoinedGroup(Group group) {
        joinedGroups = Objects.requireNonNullElseGet(joinedGroups, HashMap::new);
        joinedGroups.putIfAbsent(group.getGroupId(), group);
    }

    public void removeJoinedGroup(Group selectedGroup) {
        joinedGroups.remove(selectedGroup.getGroupId());
    }

    public void removeOwnedGroup(Group group) {

        if (role.getId() < 1) {
            return;
        }

        ownedGroups = Objects.requireNonNullElseGet(ownedGroups, HashMap::new);
        ownedGroups.remove(group.getGroupId(), group);
    }

    public String getName() {
        return firstName + " " + lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person personDAO = (Person) o;
        return getUserId().equals(personDAO.getUserId());
    }

    @Override
    public int hashCode() {
        return getUserId().hashCode();
    }

    @Override
    public String toString() {
        return "Person{" +
                "userId=" + userId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", role=" + role +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", ownedGroups=" + ownedGroups +
                ", joinedGroups=" + joinedGroups +
                '}';
    }
}