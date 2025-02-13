package application.portfolio.clientmodule.Model.Model.Task;


import application.portfolio.clientmodule.Connection.UserSession;
import application.portfolio.clientmodule.Model.Model.Person.PersonDAO;
import application.portfolio.clientmodule.Model.Request.Task.TaskRequestViewModel;
import application.portfolio.clientmodule.utils.DataParser;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class TaskDAO {

    private String taskId;
    private String title;
    private String description;
    private String createdBy;
    private String assignedTo;
    private String createdAt;
    private String deadline;
    private int taskStatus;

    public TaskDAO() {
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public int getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(int taskStatus) {
        this.taskStatus = taskStatus;
    }

    @Override
    public String toString() {
        return "TaskDAO{" +
                "taskId='" + taskId + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", assignedTo='" + assignedTo + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", deadline='" + deadline + '\'' +
                ", taskStatus=" + taskStatus +
                '}';
    }
}