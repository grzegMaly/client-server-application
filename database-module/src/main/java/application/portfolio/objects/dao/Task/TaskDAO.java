package application.portfolio.objects.dao.Task;


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

    public TaskDAO(String taskId, String title, String description,
                   String createdBy, String assignedTo, String createAt, String deadline, int taskStatus) {
        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.createdBy = createdBy;
        this.assignedTo = assignedTo;
        this.createdAt = createAt;
        this.deadline = deadline;
        this.taskStatus = taskStatus;
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

    public void setTaskStatus(int taskStatus) {
        this.taskStatus = taskStatus;
    }
}
