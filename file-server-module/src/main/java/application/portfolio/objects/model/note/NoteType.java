package application.portfolio.objects.model.note;

public enum NoteType {
    REGULAR_NOTE, DEADLINE_NOTE;

    public int getValue() {
        return this.ordinal();
    }

    public static NoteType fromValue(int value) {
        return values()[value];
    }
}
