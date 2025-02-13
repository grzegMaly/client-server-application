package application.portfolio.clientmodule.Model.Model.Task;

public enum TaskStatus {

    PENDING, IN_PROGRESS, COMPLETED, CANCELLED;

    public int getValue() {
        return this.ordinal();
    }

    public static TaskStatus fromValue(int value) {
        TaskStatus[] values = values();
        if (value < 0 || value >= values.length) {
            return null;
        }
        return values[value];
    }
}
