package application.portfolio.objects.model.note;

public enum Category {
    SHOPPING, MEETING, DIARY, RECEIPT;

    public int getValue() {
        return this.ordinal();
    }

    public static Category fromValue(int value) {
        return values()[value];
    }
}
