package application.portfolio.clientmodule.OtherElements;

import application.portfolio.clientmodule.OtherElements.temp.Objects;

import java.util.*;

public class PersonDAO {

    public enum Role {
        EMPLOYEE, MANAGER, ADMIN
    }

    private UUID id = UUID.randomUUID();
    private String firstName;
    private String lastName;
    private Role role;
    private String email;
    private String password;
    private final Set<GroupDAO> groups = new HashSet<>();
    private Set<GroupDAO> ownedGroups = null;

    public PersonDAO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public PersonDAO(String id, String firstName, String lastName, String role) {
        this.id = UUID.fromString(id);
        this.firstName = firstName;
        this.lastName = lastName;
        castRole(role);
    }

    public PersonDAO(String firstName, String lastName, String role, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        castRole(role);
    }

    private void castRole(String role) {
        try {
            this.role = Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            //TODO: Custom Window and logout
            throw new RuntimeException("Niepoprawna rola: " + role);
        }

        if (this.role == Role.MANAGER || this.role == Role.ADMIN) {
            this.ownedGroups = new HashSet<>();
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Set<GroupDAO> getGroups() {
        return new HashSet<>(groups);
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return firstName + " " + lastName;
    }

    public void addToGroup(GroupDAO... groups) {
        this.groups.addAll(Arrays.asList(groups));
    }

    public void addOwnedGroups(GroupDAO... groups) {

        if (ownedGroups != null) {
            this.ownedGroups.addAll(Arrays.asList(groups));
        }

    }


    //ToDo: Fix after connecting to the server
    public List<PersonDAO> getSubordinateUsers() {

        Set<PersonDAO> subordinates = new HashSet<>();

        List<PersonDAO> users = Objects.getPersons();

        this.ownedGroups.forEach(g -> {
            users.forEach(user -> {
                if (user.groups.contains(g)) {
                    subordinates.add(user);
                }
            });
        });

        return new ArrayList<>(subordinates);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PersonDAO personDAO = (PersonDAO) o;
        return getEmail().equals(personDAO.getEmail()) && getPassword().equals(personDAO.getPassword());
    }

    @Override
    public int hashCode() {
        int result = getEmail().hashCode();
        result = 31 * result + getPassword().hashCode();
        return result;
    }

    //Todo: Delete in the Future
    @Override
    public String toString() {
        return "PersonDAO{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", role='" + role + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", groups=" + groups +
                '}';
    }
}