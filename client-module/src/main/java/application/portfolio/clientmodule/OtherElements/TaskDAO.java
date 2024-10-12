package application.portfolio.clientmodule.OtherElements;


import java.time.LocalDate;
import java.util.UUID;

public class TaskDAO {

    private UUID id = UUID.randomUUID();
    private String title;
    private PersonDAO assignedTo;
    private PersonDAO assignedBy;
    private LocalDate createdDate;
    private LocalDate deadline;
    private String description;

    public TaskDAO(String title, PersonDAO assignedTo, PersonDAO assignedBy, LocalDate createdDate,
                   LocalDate deadline, String description) {
        this.title = title;
        this.assignedTo = assignedTo;
        this.assignedBy = assignedBy;
        this.createdDate = createdDate;
        this.deadline = deadline;
        this.description = description;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public PersonDAO getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(PersonDAO assignedTo) {
        this.assignedTo = assignedTo;
    }

    public PersonDAO getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(PersonDAO assignedBy) {
        this.assignedBy = assignedBy;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    //Todo: Delete in the Future
    @Override
    public String toString() {
        return "TaskDAO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", assignedTo=" + assignedTo +
                ", assignedBy=" + assignedBy +
                ", deadline=" + deadline +
                ", description='" + description + '\'' +
                '}';
    }
}