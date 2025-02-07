package application.portfolio.objects.model.note;

public enum Priority {
    HIGH, MEDIUM, LOW;

    public int getValue() {
        return this.ordinal();
    }

    public static Priority fromValue(int value) {
        return values()[value];
    }
}
