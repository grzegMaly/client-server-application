package application.portfolio.clientmodule.Model.Request.Task;

import application.portfolio.clientmodule.Model.Request.Task.TaskRequest.TaskRequest;
import javafx.beans.property.SimpleStringProperty;

public class TaskRequestViewModel {

    private final SimpleStringProperty title = new SimpleStringProperty();
    private final SimpleStringProperty assignedById = new SimpleStringProperty();
    private final SimpleStringProperty assignedToId = new SimpleStringProperty();
    private final SimpleStringProperty createdDate = new SimpleStringProperty();
    private final SimpleStringProperty deadline = new SimpleStringProperty();
    private final SimpleStringProperty description = new SimpleStringProperty();

    private final TaskRequestModel model = new TaskRequestModel();
    private final TaskRequestConverter converter = new TaskRequestConverter();

    public String getTitle() {
        return title.get();
    }

    public SimpleStringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getAssignedById() {
        return assignedById.get();
    }

    public SimpleStringProperty assignedByIdProperty() {
        return assignedById;
    }

    public void setAssignedById(String assignedById) {
        this.assignedById.set(assignedById);
    }

    public String getAssignedToId() {
        return assignedToId.get();
    }

    public SimpleStringProperty assignedToIdProperty() {
        return assignedToId;
    }

    public void setAssignedToId(String assignedToId) {
        this.assignedToId.set(assignedToId);
    }

    public String getCreatedDate() {
        return createdDate.get();
    }

    public SimpleStringProperty createdDateProperty() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate.set(createdDate);
    }

    public String getDeadline() {
        return deadline.get();
    }

    public SimpleStringProperty deadlineProperty() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline.set(deadline);
    }

    public String getDescription() {
        return description.get();
    }

    public SimpleStringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public void save() {
        TaskRequest data = converter.toTaskRequest(this);
        model.save(data);
    }
}