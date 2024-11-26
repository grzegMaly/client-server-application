package application.portfolio.objects.model.Person;

import application.portfolio.objects.dao.person.PersonDAO;
import application.portfolio.objects.dao.person.Role;

import java.util.*;

public class Person {

    private UUID id;
    private String firstName;
    private String lastName;
    private Role role;
    private String email;
    private String password;

    public Person() {
    }

    public Person(String firstName, String lastName, Role role) {
        this(null, firstName, lastName, role);
    }

    public Person(UUID id, String firstName, String lastName, Role role) {
        this(firstName, lastName, role, null, null);
        this.id = id;
    }

    public Person(String firstName, String lastName, Role role, String email, String password) {
        this(null, firstName, lastName, role, email, password);
    }

    public Person(UUID id, String firstName, String lastName, Role role, String email, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.email = email;
        this.password = password;
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

    public int getRole() {
        return role.getId();
    }

    public void setRole(Role role) {
        this.role = role;
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

    public static PersonDAO DAOFromPerson(Person p) {
        return new PersonDAO(p.getId(), p.getFirstName(), p.getLastName(), Role.fromId(p.getRole()));
    }

    public static Person personFromDAO(PersonDAO p) {
        return new Person(p.getId(), p.getFirstName(), p.getLastName(), p.getRole());
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", role=" + role +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
