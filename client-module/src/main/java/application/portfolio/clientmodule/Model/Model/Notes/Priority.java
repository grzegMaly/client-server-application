package application.portfolio.clientmodule.Model.Model.Notes;

public enum Priority {
    HIGH, MEDIUM, LOW;

    public int getValue() {
        return this.ordinal();
    }

    public static Priority fromValue(int value) {
        return values()[value];
    }
}
