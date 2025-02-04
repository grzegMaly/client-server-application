package application.portfolio.clientmodule.Model.Model.Person;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Role {
    EMPLOYEE(0), MANAGER(1), ADMIN(2);

    private final int id;

    Role(int id) {
        this.id = id;
    }

    @JsonValue
    public int getId() {
        return id;
    }

    @JsonCreator
    public static Role fromId(int id) {
        for (Role role : Role.values()) {
            if (role.id == id) {
                return role;
            }
        }
        throw new IllegalArgumentException("Invalid role ID: " + id);
    }
}
