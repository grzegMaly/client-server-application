package application.portfolio.objects.model;

import application.portfolio.objects.dao.person.PersonDAO;
import application.portfolio.objects.dao.person.Role;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class Person {

    private UUID id;
    private String firstName;
    private String lastName;
    private Role role;

    public Person(UUID id, String firstName, String lastName, Role role) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public void setRole(Role role) {
        this.role = role;
    }

    public static PersonDAO DAOFromPerson(Person p) {
        return new PersonDAO(p.getId(), p.getFirstName(), p.getLastName(), p.getRole());
    }

    public static Person personFromDAO(PersonDAO p) {
        return new Person(p.getId(), p.getFirstName(), p.getLastName(), p.getRole());
    }

    public static Person createPerson(ResultSet rs) throws SQLException {

        String id = rs.getString(1);
        UUID uId = UUID.fromString(id);
        String fName = rs.getString(2);
        String lName = rs.getString(3);
        int role = rs.getInt( 4);
        try {
            return new Person(uId, fName, lName, Role.fromId(role));
        } catch (Exception e) {
            return null;
        }
    }
}
