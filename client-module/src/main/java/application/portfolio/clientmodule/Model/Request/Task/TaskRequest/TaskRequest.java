package application.portfolio.clientmodule.Model.Request.Task.TaskRequest;

public class TaskRequest {

    private String title;
    private String assignedToId;
    private String assignedById;
    private String createdDate;
    private String deadline;
    private String description;

    public TaskRequest(String title, String assignedToId, String assignedById,
                       String createdDate, String deadline, String description) {
        this.title = title;
        this.assignedToId = assignedToId;
        this.assignedById = assignedById;
        this.createdDate = createdDate;
        this.deadline = deadline;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAssignedToId() {
        return assignedToId;
    }

    public void setAssignedToId(String assignedToId) {
        this.assignedToId = assignedToId;
    }

    public String getAssignedById() {
        return assignedById;
    }

    public void setAssignedById(String assignedById) {
        this.assignedById = assignedById;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "TaskRequest{" +
                "title='" + title + '\'' +
                ", assignedToId='" + assignedToId + '\'' +
                ", assignedById='" + assignedById + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", deadline='" + deadline + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}