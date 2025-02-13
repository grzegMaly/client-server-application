package application.portfolio.clientmodule.Model.Request.Task.TaskRequest;

import java.util.UUID;

public class TaskRequest {

    private UUID taskId;
    private UUID userId;

    public TaskRequest(UUID userId) {
        this(null, userId);
    }

    public TaskRequest(UUID taskId, UUID userId) {
        this.taskId = taskId;
        this.userId = userId;
    }

    public UUID getTaskId() {
        return taskId;
    }

    public void setTaskId(UUID taskId) {
        this.taskId = taskId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "TaskRequest{" +
                "taskId=" + taskId +
                ", userId=" + userId +
                '}';
    }
}