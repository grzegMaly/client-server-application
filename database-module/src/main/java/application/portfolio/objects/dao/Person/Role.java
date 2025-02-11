package application.portfolio.objects.dao.Person;

public enum Role {
    EMPLOYEE, MANAGER, ADMIN;


    public int getValue() {
        return this.ordinal();
    }

    public static Role fromValue(int value) {
        return values()[value];
    }
}
