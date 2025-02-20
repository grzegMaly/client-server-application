package application.portfolio.clientmodule.Model.Request.Task;

import application.portfolio.clientmodule.Connection.UserSession;
import application.portfolio.clientmodule.Model.Model.Person.Person;
import application.portfolio.clientmodule.Model.Model.Task.Task;
import application.portfolio.clientmodule.Model.Model.Task.TaskDAO;
import application.portfolio.clientmodule.Model.Request.Task.TaskRequest.TaskRequest;
import javafx.beans.property.SimpleStringProperty;

import java.util.List;
import java.util.UUID;

public class TaskRequestViewModel {

    private final SimpleStringProperty title = new SimpleStringProperty();
    private final SimpleStringProperty assignedTo = new SimpleStringProperty();
    private final SimpleStringProperty createdBy = new SimpleStringProperty();
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

    public String getCreatedBy() {
        return createdBy.get();
    }

    public SimpleStringProperty createdByProperty() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy.set(createdBy);
    }

    public String getAssignedTo() {
        return assignedTo.get();
    }

    public SimpleStringProperty assignedToProperty() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo.set(assignedTo);
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

        TaskDAO task = Task.createTaskDAO(this);
        model.save(task);
    }

    public List<Task> loadReceivedTasks(Person loggedInUser) {
        UUID userId = loggedInUser.getUserId();
        TaskRequest data = converter.toUserRequest(userId);
        return model.loadTasks(data, "assignedTo");
    }

    public List<Task> loadCreatedTasks(Person loggedInUser) {

        UUID userId = loggedInUser.getUserId();
        TaskRequest data = converter.toUserRequest(userId);
        return model.loadTasks(data, "createdBy");
    }

    public void deleteTask(UUID taskId) {

        UUID userId = UserSession.getInstance().getLoggedInUser().getUserId();
        TaskRequest data = converter.toUserTaskRequest(userId, taskId);
        model.deleteTask(data);
    }
}