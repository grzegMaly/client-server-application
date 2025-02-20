package application.portfolio.clientmodule.Model.Model.Task;

import application.portfolio.clientmodule.Connection.UserSession;
import application.portfolio.clientmodule.Model.Model.Person.Person;
import application.portfolio.clientmodule.Model.Request.Task.TaskRequestViewModel;
import application.portfolio.clientmodule.utils.DataParser;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class Task {

    private static final String[] REQUIRED_ATTRIBUTES = {"taskId", "title", "description", "createdBy",
            "assignedTo", "createdAt", "deadline", "taskStatus"};

    private UUID taskId;
    private String title;
    private String description;
    private Person createdBy;
    private Person assignedTo;
    private LocalDateTime createdAt;
    private LocalDate deadline;
    private TaskStatus taskStatus;

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

    public Person getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Person createdBy) {
        this.createdBy = createdBy;
    }

    public Person getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(Person assignedTo) {
        this.assignedTo = assignedTo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
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

    public static Task createTaskDAO(JsonNode node) {

        if (!DataParser.validateNode(node, REQUIRED_ATTRIBUTES)) {
            return null;
        }

        Task task = new Task();
        Iterator<Map.Entry<String, JsonNode>> iterator = node.fields();
        try {
            Person actualUser = UserSession.getInstance().getLoggedInUser();
            Person anotherPerson;
            while (iterator.hasNext()) {
                Map.Entry<String, JsonNode> entry = iterator.next();
                String key = entry.getKey();
                JsonNode value = entry.getValue();


                switch (key) {
                    case "taskId" -> {
                        UUID taskId = UUID.fromString(value.asText());
                        task.setTaskId(taskId);
                    }
                    case "title" -> task.setTitle(value.asText());
                    case "description" -> task.setDescription(value.asText());
                    case "createdBy", "assignedTo" -> {
                        UUID userId = UUID.fromString(value.asText());
                        Person person;

                        if (userId.equals(actualUser.getUserId())) {
                            person = actualUser;
                        } else {
                            person = UserSession.getPerson(userId);
                            if (person == null) {
                                person = new Person(userId);
                                UserSession.addPerson(person);
                            }
                        }

                        if ("createdBy".equals(key)) {
                            task.setCreatedBy(person);
                        } else {
                            task.setAssignedTo(person);
                        }
                    }
                    case "createdAt" -> {
                        LocalDateTime ldt = LocalDateTime.parse(value.asText());
                        task.setCreatedAt(ldt);
                    }
                    case "deadline" -> {
                        LocalDate ld = LocalDate.parse(value.asText());
                        task.setDeadline(ld);
                    }
                    case "statusCode" -> {
                        int v = value.asInt();
                        TaskStatus taskStatus = TaskStatus.fromValue(v);
                        if (taskStatus == null) {
                            throw new IllegalArgumentException();
                        }
                        task.setTaskStatus(taskStatus);
                    }
                }
            }
        } catch (IllegalArgumentException | DateTimeParseException e) {
            return null;
        }
        return task;
    }

    public static TaskDAO createTaskDAO(TaskRequestViewModel viewModel) {

        TaskDAO taskDAO = new TaskDAO();
        taskDAO.setTaskId(UUID.randomUUID().toString());
        taskDAO.setTitle(viewModel.getTitle());
        taskDAO.setDescription(viewModel.getDescription());
        taskDAO.setCreatedBy(viewModel.getCreatedBy());
        taskDAO.setAssignedTo(viewModel.getAssignedTo());
        taskDAO.setCreatedAt(viewModel.getCreatedDate());
        taskDAO.setDeadline(viewModel.getDeadline());
        taskDAO.setTaskStatus(1);

        return taskDAO;
    }
}
