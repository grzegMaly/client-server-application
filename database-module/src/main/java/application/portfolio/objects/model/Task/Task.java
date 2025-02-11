package application.portfolio.objects.model.Task;

import application.portfolio.objects.dao.DAOConverter;
import application.portfolio.objects.dao.Task.TaskDAO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class Task implements DAOConverter<Task, TaskDAO> {

    private UUID taskId;
    private String title;
    private String description;
    private UUID createdBy;
    private UUID assignedTo;
    private LocalDateTime createAt;
    private LocalDate deadline;
    private TaskStatus taskStatus;

    public Task(UUID taskId, String title, String description, UUID createdBy,
                UUID assignedTo, LocalDateTime createAt, LocalDate deadline, TaskStatus taskStatus) {
        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.createdBy = createdBy;
        this.assignedTo = assignedTo;
        this.createAt = createAt;
        this.deadline = deadline;
        this.taskStatus = taskStatus;
    }

    public Task() {

    }

    public UUID getTaskId() {
        return taskId;
    }

    public void setTaskId(UUID taskId) {
        this.taskId = taskId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

    public UUID getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(UUID assignedTo) {
        this.assignedTo = assignedTo;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    @Override
    public TaskDAO toDAO() {
        return new TaskDAO(
                getTaskId().toString(),
                getTitle(),
                getDescription(),
                getCreatedBy().toString(),
                getAssignedTo().toString(),
                getCreateAt().toString(),
                getDeadline().toString(),
                getTaskStatus().getValue());
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskId=" + taskId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", createdBy=" + createdBy +
                ", assignedTo=" + assignedTo +
                ", createAt=" + createAt +
                ", deadline=" + deadline +
                ", taskStatus=" + taskStatus +
                '}';
    }
}
